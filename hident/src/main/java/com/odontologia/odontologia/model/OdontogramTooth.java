package com.odontologia.odontologia.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "odontogram_teeth",
       uniqueConstraints = @UniqueConstraint(
               name = "uq_tooth_condition",
               columnNames = {"id_patient", "tooth_code", "nomenclature", "condition"}))
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OdontogramTooth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tooth")
    private Long idTooth;

    @ManyToOne
    @JoinColumn(name = "id_patient", nullable = false)
    private Patient patient;

    @Column(name = "tooth_code", nullable = false, length = 10)
    private String toothCode;

    @Column(name = "tooth_code_end", length = 10)
    private String toothCodeEnd;

    @Column(name = "nomenclature", nullable = false, length = 10)
    private String nomenclature;

    @Column(name = "condition", length = 30)
    private String condition;

    @Column(name = "surfaces", columnDefinition = "TEXT")
    private String surfaces;

    @Column(name = "color", length = 10)
    private String color;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
