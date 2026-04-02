package com.odontologia.odontologia.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "evolution_notes")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvolutionNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_note")
    private Long idNote;

    @ManyToOne
    @JoinColumn(name = "id_patient", nullable = false)
    private Patient patient;

    @Column(name = "note_date", nullable = false)
    private LocalDateTime noteDate;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "treatment_done", columnDefinition = "TEXT")
    private String treatmentDone;

    @Column(name = "tooth_number", length = 10)
    private String toothNumber;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @Column(name = "indications", columnDefinition = "TEXT")
    private String indications;

    @Column(name = "next_appointment", columnDefinition = "TEXT")
    private String nextAppointment;

    @Column(name = "appointment_scheduled")
    private Boolean appointmentScheduled = false;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.noteDate == null) {
            this.noteDate = LocalDateTime.now();
        }
    }
}
