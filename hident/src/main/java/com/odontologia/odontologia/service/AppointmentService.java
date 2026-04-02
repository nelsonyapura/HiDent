package com.odontologia.odontologia.service;

import com.odontologia.odontologia.model.Appointment;
import com.odontologia.odontologia.model.Patient;
import com.odontologia.odontologia.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public Optional<Appointment> findById(Long id) {
        return appointmentRepository.findById(id);
    }

    public List<Appointment> findByDateRange(LocalDate start, LocalDate end) {
        return appointmentRepository
                .findByAppointmentDateBetweenOrderByAppointmentDateAscStartTimeAsc(start, end);
    }

    public List<Appointment> findByDateRangeAndStatus(LocalDate start, LocalDate end, String status) {
        if (status == null || status.isBlank() || "TODOS".equals(status)) {
            return findByDateRange(start, end);
        }
        return appointmentRepository
                .findByAppointmentDateBetweenAndStatusOrderByAppointmentDateAscStartTimeAsc(start, end, status);
    }

    public List<Appointment> findByPatient(Long idPatient) {
        return appointmentRepository
                .findByPatientIdPatientOrderByAppointmentDateDescStartTimeDesc(idPatient);
    }

    public List<String> findDistinctProfessionals() {
        return appointmentRepository.findDistinctProfessionals();
    }

    public Optional<Appointment> findLastVisit(Long patientId) {
        List<Appointment> list = appointmentRepository.findLastVisitByPatient(patientId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public Optional<Appointment> findNextAppointment(Long patientId) {
        List<Appointment> list = appointmentRepository.findNextAppointmentByPatient(patientId, LocalDate.now());
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public Appointment create(Patient patient, LocalDate date, LocalTime startTime,
                               LocalTime endTime, String reason, String notes,
                               String assignedTo, String createdBy) {

        validateTimes(startTime, endTime);
        checkOverlap(date, startTime, endTime, null);

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setAppointmentDate(date);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setReason(clean(reason));
        appointment.setNotes(clean(notes));
        appointment.setAssignedTo(clean(assignedTo));
        appointment.setCreatedBy(clean(createdBy));
        appointment.setStatus("PROGRAMADA");

        return appointmentRepository.save(appointment);
    }

    public Appointment update(Long id, LocalDate date, LocalTime startTime,
                               LocalTime endTime, String reason, String notes,
                               String assignedTo, String updatedBy) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if ("CANCELADA".equals(appointment.getStatus()) || "ATENDIDA".equals(appointment.getStatus())) {
            throw new RuntimeException("No se puede editar una cita " + appointment.getStatusLabel().toLowerCase());
        }

        validateTimes(startTime, endTime);
        checkOverlap(date, startTime, endTime, id);

        appointment.setAppointmentDate(date);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setReason(clean(reason));
        appointment.setNotes(clean(notes));
        appointment.setAssignedTo(clean(assignedTo));
        appointment.setUpdatedBy(clean(updatedBy));

        return appointmentRepository.save(appointment);
    }

    public Appointment changeStatus(Long id, String newStatus, String updatedBy) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        validateStatusTransition(appointment.getStatus(), newStatus);

        appointment.setStatus(newStatus);
        appointment.setUpdatedBy(clean(updatedBy));

        return appointmentRepository.save(appointment);
    }

    public void delete(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Cita no encontrada");
        }
        appointmentRepository.deleteById(id);
    }

    private void validateTimes(LocalTime start, LocalTime end) {
        if (start == null) throw new RuntimeException("La hora de inicio es obligatoria");
        if (end == null) throw new RuntimeException("La hora de fin es obligatoria");
        if (!end.isAfter(start)) throw new RuntimeException("La hora de fin debe ser posterior a la de inicio");
    }

    private void checkOverlap(LocalDate date, LocalTime start, LocalTime end, Long excludeId) {
        List<Appointment> overlapping = appointmentRepository.findOverlapping(date, start, end, excludeId);
        if (!overlapping.isEmpty()) {
            Appointment conflict = overlapping.get(0);
            throw new RuntimeException("Horario en conflicto con la cita de " +
                    conflict.getPatient().getFullName() + " (" +
                    conflict.getStartTimeFormatted() + " - " + conflict.getEndTimeFormatted() + ")");
        }
    }

    private void validateStatusTransition(String current, String next) {

        if ("ATENDIDA".equals(current) || "NO_ASISTIO".equals(current) || "CANCELADA".equals(current)) {
            throw new RuntimeException("No se puede cambiar el estado de una cita " +
                    current.toLowerCase().replace("_", " "));
        }
        if ("PROGRAMADA".equals(current) && !List.of("CONFIRMADA", "CANCELADA").contains(next)) {
            throw new RuntimeException("Una cita programada solo puede confirmarse o cancelarse");
        }
        if ("CONFIRMADA".equals(current) && !List.of("ATENDIDA", "NO_ASISTIO", "CANCELADA").contains(next)) {
            throw new RuntimeException("Una cita confirmada solo puede atenderse, marcarse como no asistió o cancelarse");
        }
    }

    private String clean(String val) {
        return val != null && !val.isBlank() ? val.trim() : null;
    }
}
