package com.odontologia.odontologia.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Period;

@Entity
@Table(name = "patients")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_patient")
    private Long idPatient;

    @Column(name = "dni", nullable = false, unique = true, length = 8)
    private String dni;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "gender", length = 1)
    private String gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "age")
    private Integer age;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "medical_notes", columnDefinition = "TEXT")
    private String medicalNotes;

    @Column(name = "dental_history", columnDefinition = "TEXT")
    private String dentalHistory;

    @Column(name = "guardian_name", length = 150)
    private String guardianName;

    @Column(name = "guardian_phone", length = 20)
    private String guardianPhone;

    @Column(name = "emergency_phone", length = 20)
    private String emergencyPhone;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        recalculateAge();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Transient
    public String getBirthDateFormatted() {
        return birthDate != null ? birthDate.format(DATE_FMT) : null;
    }

    @Transient
    public String getCreatedAtFormatted() {
        return createdAt != null ? createdAt.format(DATETIME_FMT) : null;
    }

    @Transient
    public String getUpdatedAtFormatted() {
        return updatedAt != null ? updatedAt.format(DATETIME_FMT) : null;
    }

    @Transient
    public Integer getEffectiveAge() {
        if (age != null) return age;
        if (birthDate == null) return null;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public void recalculateAge() {
        if (birthDate != null) {
            this.age = Period.between(birthDate, LocalDate.now()).getYears();
        }
    }

    @Transient
    public boolean isMinor() {
        Integer effectiveAge = getEffectiveAge();
        return effectiveAge != null && effectiveAge < 18;
    }

    @Transient
    public String getGenderLabel() {
        if ("M".equals(gender)) return "Masculino";
        if ("F".equals(gender)) return "Femenino";
        return null;
    }
}
