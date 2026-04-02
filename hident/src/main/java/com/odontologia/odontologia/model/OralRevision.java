package com.odontologia.odontologia.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "oral_revisions")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OralRevision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_revision")
    private Long idRevision;

    @ManyToOne
    @JoinColumn(name = "id_patient", nullable = false)
    private Patient patient;

    @Column(name = "labios", length = 255)
    private String labios = "Sin alteraciones";

    @Column(name = "carrillos", length = 255)
    private String carrillos = "Sin alteraciones";

    @Column(name = "paladar_duro", length = 255)
    private String paladarDuro = "Sin alteraciones";

    @Column(name = "paladar_blando", length = 255)
    private String paladarBlando = "Sin alteraciones";

    @Column(name = "encias", length = 255)
    private String encias = "Sin alteraciones";

    @Column(name = "lengua", length = 255)
    private String lengua = "Sin alteraciones";

    @Column(name = "piso_boca", length = 255)
    private String pisoBoca = "Sin alteraciones";

    @Column(name = "orofaringe", length = 255)
    private String orofaringe = "Sin alteraciones";

    @Column(name = "atm", length = 255)
    private String atm = "Sin alteraciones";

    @Column(name = "higiene", length = 50)
    private String higiene = "Regular";

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @Column(name = "created_by", length = 100)
    private String createdBy;

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
