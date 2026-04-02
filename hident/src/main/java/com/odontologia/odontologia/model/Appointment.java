package com.odontologia.odontologia.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "appointments")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_appointment")
    private Long idAppointment;

    @ManyToOne
    @JoinColumn(name = "id_patient", nullable = false)
    private Patient patient;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PROGRAMADA";

    @Column(name = "assigned_to", length = 100)
    private String assignedTo;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transient
    public String getStartTimeFormatted() {
        return startTime != null ? startTime.format(TIME_FMT) : null;
    }

    @Transient
    public String getEndTimeFormatted() {
        return endTime != null ? endTime.format(TIME_FMT) : null;
    }

    @Transient
    public String getAppointmentDateFormatted() {
        return appointmentDate != null ? appointmentDate.format(DATE_FMT) : null;
    }

    @Transient
    public String getStatusLabel() {
        return switch (status) {
            case "PROGRAMADA"  -> "Programada";
            case "CONFIRMADA"  -> "Confirmada";
            case "ATENDIDA"    -> "Atendida";
            case "NO_ASISTIO"  -> "No asistió";
            case "CANCELADA"   -> "Cancelada";
            default            -> status;
        };
    }
}
