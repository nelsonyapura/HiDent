package com.odontologia.odontologia.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "consents")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Consent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consent")
    private Long idConsent;

    @ManyToOne
    @JoinColumn(name = "id_patient", nullable = false)
    private Patient patient;

    @Column(name = "consent_type", nullable = false, length = 100)
    private String consentType;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "stored_name", length = 255)
    private String storedName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "signed")
    private Boolean signed = false;

    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Transient
    public String getFileSizeFormatted() {
        if (fileSize == null) return "-";
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
        return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
    }

    @Transient
    public boolean hasFile() {
        return storedName != null && !storedName.isBlank();
    }

    @Transient
    public boolean isImage() {
        if (fileName == null) return false;
        String lower = fileName.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png");
    }

    @Transient
    public boolean isPdf() {
        if (fileName == null) return false;
        return fileName.toLowerCase().endsWith(".pdf");
    }
}
