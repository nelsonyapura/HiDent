package com.odontologia.odontologia.service;

import com.odontologia.odontologia.model.Appointment;
import com.odontologia.odontologia.model.EvolutionNote;
import com.odontologia.odontologia.model.Patient;
import com.odontologia.odontologia.repository.EvolutionNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvolutionNoteService {

    private final EvolutionNoteRepository evolutionNoteRepository;
    private final AppointmentService appointmentService;

    public List<EvolutionNote> findByPatientId(Long idPatient) {
        return evolutionNoteRepository.findByPatientIdPatientOrderByNoteDateDesc(idPatient);
    }

    @Transactional
    public EvolutionNote addNote(Patient patient, String reason, String diagnosis,
                                  String treatmentDone, String toothNumber,
                                  String observations, String indications,
                                  String nextAppointment,
                                  boolean scheduleNextAppt,
                                  String nextApptDate, String nextApptStart,
                                  String nextApptEnd, String nextApptReason,
                                  String nextApptDoctor,
                                  String createdBy) {

        if (reason == null || reason.isBlank()) {
            throw new RuntimeException("El motivo de consulta es obligatorio");
        }

        EvolutionNote note = new EvolutionNote();
        note.setPatient(patient);
        note.setNoteDate(LocalDateTime.now());
        note.setReason(reason.trim());
        note.setDiagnosis(clean(diagnosis));
        note.setTreatmentDone(clean(treatmentDone));
        note.setToothNumber(clean(toothNumber));
        note.setObservations(clean(observations));
        note.setIndications(clean(indications));
        note.setCreatedBy(clean(createdBy));
        note.setAppointmentScheduled(false);

        if (scheduleNextAppt && nextApptDate != null && !nextApptDate.isBlank()
                && nextApptStart != null && !nextApptStart.isBlank()
                && nextApptEnd != null && !nextApptEnd.isBlank()) {

            LocalDate apptDate = LocalDate.parse(nextApptDate);
            LocalTime apptStart = LocalTime.parse(nextApptStart);
            LocalTime apptEnd = LocalTime.parse(nextApptEnd);
            String apptReason = clean(nextApptReason);

            Appointment appointment = appointmentService.create(
                    patient, apptDate, apptStart, apptEnd,
                    apptReason, "Agendada desde nota de evolución",
                    clean(nextApptDoctor), createdBy);

            note.setAppointmentScheduled(true);

            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");
            String apptDesc = apptDate.format(dateFmt) + " de "
                    + apptStart.format(timeFmt) + " a " + apptEnd.format(timeFmt);
            if (apptReason != null) {
                apptDesc += " — " + apptReason;
            }
            if (clean(nextApptDoctor) != null) {
                apptDesc += " (Dr. " + clean(nextApptDoctor) + ")";
            }
            note.setNextAppointment(apptDesc);

        } else {

            note.setNextAppointment(clean(nextAppointment));
        }

        return evolutionNoteRepository.save(note);
    }

    public EvolutionNote addNote(Patient patient, String reason, String diagnosis,
                                  String treatmentDone, String toothNumber,
                                  String observations, String nextAppointment,
                                  String createdBy) {
        return addNote(patient, reason, diagnosis, treatmentDone, toothNumber,
                observations, null, nextAppointment,
                false, null, null, null, null, null, createdBy);
    }

    @Transactional
    public EvolutionNote updateNote(Long noteId, Long patientId,
                                     String reason, String diagnosis,
                                     String treatmentDone, String toothNumber,
                                     String observations, String indications,
                                     String nextAppointment) {

        EvolutionNote note = evolutionNoteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Nota de evolución no encontrada"));

        if (!note.getPatient().getIdPatient().equals(patientId)) {
            throw new RuntimeException("La nota no pertenece a este paciente");
        }

        if (reason == null || reason.isBlank()) {
            throw new RuntimeException("El motivo de consulta es obligatorio");
        }

        note.setReason(reason.trim());
        note.setDiagnosis(clean(diagnosis));
        note.setTreatmentDone(clean(treatmentDone));
        note.setToothNumber(clean(toothNumber));
        note.setObservations(clean(observations));
        note.setIndications(clean(indications));
        note.setNextAppointment(clean(nextAppointment));

        return evolutionNoteRepository.save(note);
    }

    @Transactional
    public void deleteNote(Long noteId, Long patientId) {
        EvolutionNote note = evolutionNoteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Nota de evolución no encontrada"));

        if (!note.getPatient().getIdPatient().equals(patientId)) {
            throw new RuntimeException("La nota no pertenece a este paciente");
        }

        evolutionNoteRepository.delete(note);
    }

    private String clean(String val) { return val != null && !val.isBlank() ? val.trim() : null; }
}
