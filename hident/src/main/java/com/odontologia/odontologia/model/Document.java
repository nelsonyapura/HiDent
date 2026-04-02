package com.odontologia.odontologia.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_document")
    private Long idDocument;

    @ManyToOne
    @JoinColumn(name = "id_patient", nullable = false)
    private Patient patient;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "stored_name", nullable = false, length = 255)
    private String storedName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type", length = 10)
    private String fileType = "PDF";

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "uploaded_by", length = 100)
    private String uploadedBy;

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
    public boolean isImage() {
        return fileType != null && (fileType.equalsIgnoreCase("JPG")
                || fileType.equalsIgnoreCase("JPEG")
                || fileType.equalsIgnoreCase("PNG"));
    }

    @Transient
    public boolean isPdf() {
        return fileType == null || fileType.equalsIgnoreCase("PDF");
    }
}
