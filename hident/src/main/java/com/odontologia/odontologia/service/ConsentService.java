package com.odontologia.odontologia.service;

import com.odontologia.odontologia.model.Consent;
import com.odontologia.odontologia.model.Patient;
import com.odontologia.odontologia.repository.ConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsentService {

    private final ConsentRepository consentRepository;

    @Value("${app.upload.dir:uploads/documents}")
    private String uploadDir;

    private Path consentsDir;

    @PostConstruct
    public void init() {
        try {
            consentsDir = Paths.get(uploadDir, "consents");
            Files.createDirectories(consentsDir);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de consentimientos", e);
        }
    }

    public List<Consent> findByPatientId(Long idPatient) {
        return consentRepository.findByPatientIdPatientOrderByCreatedAtDesc(idPatient);
    }

    public Optional<Consent> findById(Long id) {
        return consentRepository.findById(id);
    }

    public Consent upload(Patient patient, MultipartFile file, String consentType,
                          String notes, String createdBy) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Debe seleccionar un archivo");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            throw new RuntimeException("Nombre de archivo inválido");
        }

        String lowerName = originalName.toLowerCase();
        if (!lowerName.endsWith(".pdf") && !lowerName.endsWith(".jpg")
                && !lowerName.endsWith(".jpeg") && !lowerName.endsWith(".png")) {
            throw new RuntimeException("Solo se permiten archivos PDF, JPG o PNG");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("El archivo no puede superar los 10MB");
        }

        String extension = lowerName.substring(lowerName.lastIndexOf('.'));
        String storedName = UUID.randomUUID() + extension;
        Path filePath = consentsDir.resolve(storedName);
        Files.copy(file.getInputStream(), filePath);

        Consent consent = new Consent();
        consent.setPatient(patient);
        consent.setConsentType(consentType != null && !consentType.isBlank() ? consentType.trim() : "General");
        consent.setFileName(originalName);
        consent.setStoredName(storedName);
        consent.setFileSize(file.getSize());
        consent.setSigned(true);
        consent.setSignedAt(LocalDateTime.now());
        consent.setNotes(notes != null && !notes.isBlank() ? notes.trim() : null);
        consent.setCreatedBy(createdBy);

        return consentRepository.save(consent);
    }

    public Consent markAsSigned(Long idConsent) {
        Consent consent = consentRepository.findById(idConsent)
                .orElseThrow(() -> new RuntimeException("Consentimiento no encontrado"));
        consent.setSigned(true);
        consent.setSignedAt(LocalDateTime.now());
        return consentRepository.save(consent);
    }

    public Optional<Path> getFilePath(Long idConsent) {
        return consentRepository.findById(idConsent)
                .filter(c -> c.getStoredName() != null)
                .map(c -> consentsDir.resolve(c.getStoredName()))
                .filter(Files::exists);
    }

    public void delete(Long idConsent, Long idPatient) {
        Consent consent = consentRepository.findById(idConsent)
                .orElseThrow(() -> new RuntimeException("Consentimiento no encontrado"));

        if (!consent.getPatient().getIdPatient().equals(idPatient)) {
            throw new RuntimeException("El consentimiento no pertenece a este paciente");
        }

        if (consent.getStoredName() != null) {
            try {
                Path filePath = consentsDir.resolve(consent.getStoredName());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {

            }
        }

        consentRepository.delete(consent);
    }

    public List<String> getConsentTypes() {
        return List.of(
                "General",
                "Exodoncia",
                "Endodoncia",
                "Prótesis",
                "Ortodoncia",
                "Cirugía Oral",
                "Blanqueamiento",
                "Radiografía",
                "Otro"
        );
    }
}
