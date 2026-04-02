package com.odontologia.odontologia.service;

import com.odontologia.odontologia.model.Document;
import com.odontologia.odontologia.model.Patient;
import com.odontologia.odontologia.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Value("${app.upload.dir:uploads/documents}")
    private String uploadDir;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".pdf", ".jpg", ".jpeg", ".png"
    );

    private static final java.util.Map<String, String> EXTENSION_TO_TYPE = java.util.Map.of(
            ".pdf", "PDF",
            ".jpg", "JPG",
            ".jpeg", "JPG",
            ".png", "PNG"
    );

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de uploads: " + uploadDir, e);
        }
    }

    public List<Document> findByPatientId(Long idPatient) {
        return documentRepository.findByPatientIdPatientOrderByCreatedAtDesc(idPatient);
    }

    public Document upload(Patient patient, MultipartFile file, String description,
                           String category, String uploadedBy) throws IOException {

        if (file.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            throw new RuntimeException("Nombre de archivo inválido");
        }

        String lowerName = originalName.toLowerCase();
        String extension = lowerName.substring(lowerName.lastIndexOf('.'));

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new RuntimeException("Solo se permiten archivos PDF, JPG o PNG");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("El archivo no puede superar los 10MB");
        }

        String storedName = UUID.randomUUID() + extension;
        Path filePath = Paths.get(uploadDir).resolve(storedName);
        Files.copy(file.getInputStream(), filePath);

        String fileType = EXTENSION_TO_TYPE.getOrDefault(extension, "PDF");

        Document doc = new Document();
        doc.setPatient(patient);
        doc.setFileName(originalName);
        doc.setStoredName(storedName);
        doc.setFileSize(file.getSize());
        doc.setFileType(fileType);
        doc.setDescription(description != null && !description.isBlank() ? description.trim() : null);
        doc.setCategory(category != null && !category.isBlank() ? category.trim() : "OTRO");
        doc.setUploadedBy(uploadedBy);

        return documentRepository.save(doc);
    }

    public Optional<Path> getFilePath(Long idDocument, Long idPatient) {
        return documentRepository.findByIdDocumentAndPatientIdPatient(idDocument, idPatient)
                .map(doc -> Paths.get(uploadDir).resolve(doc.getStoredName()))
                .filter(Files::exists);
    }

    public Optional<Document> findByIdAndPatient(Long idDocument, Long idPatient) {
        return documentRepository.findByIdDocumentAndPatientIdPatient(idDocument, idPatient);
    }

    public void delete(Long idDocument, Long idPatient) {
        Document doc = documentRepository.findByIdDocumentAndPatientIdPatient(idDocument, idPatient)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        try {
            Path filePath = Paths.get(uploadDir).resolve(doc.getStoredName());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {

        }

        documentRepository.delete(doc);
    }
}
