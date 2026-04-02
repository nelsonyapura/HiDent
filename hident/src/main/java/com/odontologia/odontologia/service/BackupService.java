package com.odontologia.odontologia.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;

import com.google.auth.oauth2.UserCredentials;
import java.nio.file.Files;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupService {

    private final JavaMailSender mailSender;

    @Value("${backup.dir}")
    private String backupDir;

    @Value("${backup.db.name}")
    private String dbName;

    @Value("${backup.db.user}")
    private String dbUser;

    @Value("${backup.db.password}")
    private String dbPassword;

    @Value("${backup.pg-dump}")
    private String pgDumpPath;

    @Value("${backup.drive.folder-id}")
    private String driveFolderId;

    @Value("${backup.drive.credentials}")
    private String driveCredentials;

    @Value("${backup.drive.max-files}")
    private int maxDriveFiles;

    @Value("${spring.mail.username}")
    private String smtpEmail;

    @Value("${backup.notify.email}")
    private String notifyEmail;

    public BackupResult runFullBackup(String triggeredBy) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
        String filename = "hident_backup_" + timestamp + ".sql";

        log.info(" Backup iniciado por: {} ", triggeredBy);

        try {
            Files.createDirectories(Paths.get(backupDir));
        } catch (IOException e) {
            return fail(filename, "No se pudo crear directorio: " + e.getMessage(), now);
        }

        log.info("[1/4] Ejecutando pg_dump...");
        String filePath = backupDir + "/" + filename;
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    pgDumpPath, "-U", dbUser, "-d", dbName, "-F", "p", "-f", filePath
            );
            pb.environment().put("PGPASSWORD", dbPassword);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            String output;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                output = reader.lines().collect(Collectors.joining("\n"));
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return fail(filename, "pg_dump falló (código " + exitCode + "): " + output, now);
            }
            log.info("  pg_dump OK: {}", filename);
        } catch (Exception e) {
            return fail(filename, "Error ejecutando pg_dump: " + e.getMessage(), now);
        }

        log.info("[2/4] Limpiando backups locales...");
        cleanLocalBackups(30);

        log.info("[3/4] Subiendo a Google Drive...");
        try {
            Drive driveService = getDriveService();
            uploadToDrive(driveService, filePath, filename);
            cleanDriveBackups(driveService);
            log.info("  Drive OK");
        } catch (Exception e) {
            log.error("  Error Drive: {}", e.getMessage());

            sendEmail(filename, getFileSizeMB(filePath), false,
                    "Backup local OK pero falló subida a Drive: " + e.getMessage(), now);
            return new BackupResult(true, filename, getFileSizeMB(filePath),
                    "Backup local OK. Drive falló: " + e.getMessage());
        }

        log.info("[4/4] Enviando notificación...");
        double sizeMB = getFileSizeMB(filePath);
        sendEmail(filename, sizeMB, true, null, now);

        log.info(" Backup completado: {} ({} MB) ", filename, sizeMB);
        return new BackupResult(true, filename, sizeMB, "Backup completado exitosamente");
    }

            private Drive getDriveService() throws Exception {
            String tokenContent = Files.readString(Paths.get(driveCredentials));

            com.google.gson.JsonObject json = com.google.gson.JsonParser
                    .parseString(tokenContent).getAsJsonObject();

            String clientId     = json.get("client_id").getAsString();
            String clientSecret = json.get("client_secret").getAsString();
            String refreshToken = json.get("refresh_token").getAsString();

            com.google.auth.oauth2.UserCredentials credentials =
                    com.google.auth.oauth2.UserCredentials.newBuilder()
                            .setClientId(clientId)
                            .setClientSecret(clientSecret)
                            .setRefreshToken(refreshToken)
                            .build();

            credentials.refreshIfExpired();

            return new Drive.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName("HiDent Backup")
                    .build();
        }

    private void uploadToDrive(Drive service, String filePath, String filename) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(filename);
        fileMetadata.setParents(Collections.singletonList(driveFolderId));

        FileContent mediaContent = new FileContent("application/sql", new java.io.File(filePath));
        service.files().create(fileMetadata, mediaContent)
                .setFields("id, name")
                .execute();
    }

    private void cleanDriveBackups(Drive service) throws IOException {
        FileList result = service.files().list()
                .setQ("'" + driveFolderId + "' in parents and name contains 'hident_backup_' and trashed=false")
                .setFields("files(id, name, createdTime)")
                .setOrderBy("createdTime desc")
                .setPageSize(100)
                .execute();

        List<File> files = result.getFiles();
        if (files != null && files.size() > maxDriveFiles) {
            for (int i = maxDriveFiles; i < files.size(); i++) {
                try {
                    service.files().delete(files.get(i).getId()).execute();
                    log.info("  Eliminado de Drive: {}", files.get(i).getName());
                } catch (Exception e) {
                    log.warn("  No se pudo eliminar: {}", files.get(i).getName());
                }
            }
        }
    }

    private void cleanLocalBackups(int maxFiles) {
        try (Stream<Path> files = Files.list(Paths.get(backupDir))) {
            List<Path> backups = files
                    .filter(p -> p.getFileName().toString().startsWith("hident_backup_"))
                    .sorted(Comparator.comparingLong(p -> {
                        try { return -Files.getLastModifiedTime(p).toMillis(); }
                        catch (IOException e) { return 0; }
                    }))
                    .collect(Collectors.toList());

            for (int i = maxFiles; i < backups.size(); i++) {
                try {
                    Files.delete(backups.get(i));
                    log.info("  Eliminado local: {}", backups.get(i).getFileName());
                } catch (IOException e) {
                    log.warn("  No se pudo eliminar: {}", backups.get(i).getFileName());
                }
            }
        } catch (IOException e) {
            log.warn("Error limpiando backups locales: {}", e.getMessage());
        }
    }

    private void sendEmail(String filename, double sizeMB, boolean success,
                           String errorMsg, LocalDateTime now) {
        try {
            String status = success ? "COMPLETADO" : "ERROR";
            String statusColor = success ? "#0D9488" : "#EF4444";
            String subject = success
                    ? "HiDent Backup Exitoso - " + now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "HiDent Backup Fallido - " + now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String ticketId = "BKP-" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));

            String errorHtml = errorMsg != null
                    ? "<div style='padding:0 24px 16px 24px;'><p style='font-family:Courier New,monospace;" +
                      "color:#EF4444;font-size:11px;margin:0;padding:10px;background:#FEF2F2;" +
                      "border:1px dashed #FECACA;'>Error: " + errorMsg + "</p></div>"
                    : "";

            String html = """
                <html>
                <body style="font-family:Arial,sans-serif;background-color:#E8EDDF;padding:30px;">
                <div style="max-width:420px;margin:0 auto;background:#FFFEF9;border:1px solid #D4D0C8;box-shadow:2px 2px 10px rgba(0,0,0,0.08);">
                    <div style="text-align:center;padding:24px 24px 0 24px;">
                        <p style="font-family:'Courier New',monospace;font-size:11px;color:#0D9488;letter-spacing:3px;margin:0;">— BACKUP —</p>
                        <p style="border-bottom:2px dashed #CCFBF1;margin:10px 0 0 0;"></p>
                    </div>
                    <div style="text-align:center;padding:16px 24px;">
                        <h1 style="font-family:Georgia,serif;font-size:22px;color:#115E59;margin:0;">Hi Dent</h1>
                        <p style="font-family:'Courier New',monospace;font-size:11px;color:#64748B;margin:4px 0 0;">Ticket: %s</p>
                    </div>
                    <p style="border-bottom:1px dashed #CCFBF1;margin:0 24px;"></p>
                    <div style="padding:16px 24px;">
                        <table style="width:100%%;font-family:'Courier New',monospace;font-size:12px;">
                            <tr><td style="padding:6px 0;color:#94A3B8;font-size:10px;text-transform:uppercase;letter-spacing:1px;">Fecha:</td>
                                <td style="padding:6px 0;color:#94A3B8;font-size:10px;text-transform:uppercase;letter-spacing:1px;">Hora:</td></tr>
                            <tr><td style="padding:0 0 12px 0;color:#115E59;font-size:14px;font-weight:bold;">%s</td>
                                <td style="padding:0 0 12px 0;color:#115E59;font-size:14px;font-weight:bold;">%s</td></tr>
                        </table>
                        <p style="border-bottom:1px dashed #CCFBF1;margin:0 0 12px 0;"></p>
                        <table style="width:100%%;font-family:'Courier New',monospace;font-size:12px;">
                            <tr><td style="padding:6px 0;color:#94A3B8;font-size:10px;text-transform:uppercase;letter-spacing:1px;">Archivo:</td>
                                <td style="padding:6px 0;color:#94A3B8;font-size:10px;text-transform:uppercase;letter-spacing:1px;">Tamaño:</td></tr>
                            <tr><td style="padding:0 0 12px 0;color:#115E59;font-size:12px;font-weight:bold;">%s</td>
                                <td style="padding:0 0 12px 0;color:#115E59;font-size:14px;font-weight:bold;">%.2f MB</td></tr>
                        </table>
                        <p style="border-bottom:1px dashed #CCFBF1;margin:0 0 12px 0;"></p>
                        <table style="width:100%%;font-family:'Courier New',monospace;font-size:12px;">
                            <tr><td style="padding:6px 0;color:#94A3B8;font-size:10px;text-transform:uppercase;letter-spacing:1px;">Destino:</td></tr>
                            <tr><td style="padding:0 0 12px 0;color:#115E59;font-size:12px;">Google Drive / HiDent - Backup</td></tr>
                        </table>
                    </div>
                    <div style="padding:0 24px 20px 24px;">
                        <div style="background:%s;text-align:center;padding:12px;border-radius:2px;">
                            <span style="font-family:'Courier New',monospace;color:white;font-size:13px;font-weight:bold;letter-spacing:2px;">%s</span>
                        </div>
                    </div>
                    %s
                    <div style="text-align:center;padding:12px 24px 20px 24px;border-top:2px dashed #CCFBF1;">
                        <p style="font-family:'Courier New',monospace;font-size:10px;color:#94A3B8;margin:8px 0 0;letter-spacing:1px;">HiDent Odontologia v1.0</p>
                    </div>
                </div>
                </body>
                </html>
                """.formatted(
                    ticketId,
                    now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    now.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                    filename, sizeMB,
                    statusColor, status,
                    errorHtml
            );

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(smtpEmail);
            helper.setTo(notifyEmail);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("  Email enviado a {}", notifyEmail);
        } catch (Exception e) {
            log.error("  Error enviando email: {}", e.getMessage());
        }
    }

    private double getFileSizeMB(String filePath) {
        try {
            return Math.round(Files.size(Paths.get(filePath)) / (1024.0 * 1024.0) * 100.0) / 100.0;
        } catch (IOException e) {
            return 0;
        }
    }

    private BackupResult fail(String filename, String error, LocalDateTime now) {
        log.error("  FALLO: {}", error);
        sendEmail(filename, 0, false, error, now);
        return new BackupResult(false, filename, 0, error);
    }

    public record BackupResult(boolean success, String filename, double sizeMB, String message) {}
}
