package com.odontologia.odontologia.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "anamnesis")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Anamnesis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_anamnesis")
    private Long idAnamnesis;

    @OneToOne
    @JoinColumn(name = "id_patient", nullable = false, unique = true)
    private Patient patient;

    @Column(name = "hypertension")
    private Boolean hypertension = false;

    @Column(name = "diabetes")
    private Boolean diabetes = false;

    @Column(name = "heart_disease")
    private Boolean heartDisease = false;

    @Column(name = "hepatitis")
    private Boolean hepatitis = false;

    @Column(name = "hiv")
    private Boolean hiv = false;

    @Column(name = "tuberculosis")
    private Boolean tuberculosis = false;

    @Column(name = "asthma")
    private Boolean asthma = false;

    @Column(name = "epilepsy")
    private Boolean epilepsy = false;

    @Column(name = "kidney_disease")
    private Boolean kidneyDisease = false;

    @Column(name = "bleeding_disorders")
    private Boolean bleedingDisorders = false;

    @Column(name = "other_conditions", columnDefinition = "TEXT")
    private String otherConditions;

    @Column(name = "current_medications", columnDefinition = "TEXT")
    private String currentMedications;

    @Column(name = "allergy_penicillin")
    private Boolean allergyPenicillin = false;

    @Column(name = "allergy_latex")
    private Boolean allergyLatex = false;

    @Column(name = "allergy_anesthesia")
    private Boolean allergyAnesthesia = false;

    @Column(name = "allergy_nsaids")
    private Boolean allergyNsaids = false;

    @Column(name = "other_allergies", columnDefinition = "TEXT")
    private String otherAllergies;

    @Column(name = "smoker")
    private Boolean smoker = false;

    @Column(name = "alcohol")
    private Boolean alcohol = false;

    @Column(name = "bruxism")
    private Boolean bruxism = false;

    @Column(name = "previous_surgeries", columnDefinition = "TEXT")
    private String previousSurgeries;

    @Column(name = "last_dental_visit", length = 100)
    private String lastDentalVisit;

    @Column(name = "pregnant")
    private Boolean pregnant = false;

    @Column(name = "breastfeeding")
    private Boolean breastfeeding = false;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

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
