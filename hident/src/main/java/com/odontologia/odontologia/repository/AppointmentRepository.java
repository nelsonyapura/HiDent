package com.odontologia.odontologia.repository;

import com.odontologia.odontologia.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAscStartTimeAsc(
            LocalDate start, LocalDate end);

    List<Appointment> findByAppointmentDateOrderByStartTimeAsc(LocalDate date);

    List<Appointment> findByPatientIdPatientOrderByAppointmentDateDescStartTimeDesc(Long idPatient);

    List<Appointment> findByAppointmentDateBetweenAndStatusOrderByAppointmentDateAscStartTimeAsc(
            LocalDate start, LocalDate end, String status);

    List<Appointment> findByAppointmentDateBetweenAndAssignedToOrderByAppointmentDateAscStartTimeAsc(
            LocalDate start, LocalDate end, String assignedTo);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate = :date " +
           "AND a.status NOT IN ('CANCELADA', 'NO_ASISTIO') " +
           "AND a.startTime < :endTime AND a.endTime > :startTime " +
           "AND (:excludeId IS NULL OR a.idAppointment != :excludeId)")
    List<Appointment> findOverlapping(@Param("date") LocalDate date,
                                       @Param("startTime") LocalTime startTime,
                                       @Param("endTime") LocalTime endTime,
                                       @Param("excludeId") Long excludeId);

    @Query("SELECT DISTINCT a.assignedTo FROM Appointment a WHERE a.assignedTo IS NOT NULL ORDER BY a.assignedTo")
    List<String> findDistinctProfessionals();

    @Query("SELECT a FROM Appointment a WHERE a.patient.idPatient = :patientId " +
           "AND a.status = 'ATENDIDA' ORDER BY a.appointmentDate DESC, a.startTime DESC")
    List<Appointment> findLastVisitByPatient(@Param("patientId") Long patientId);

    @Query("SELECT a FROM Appointment a WHERE a.patient.idPatient = :patientId " +
           "AND a.status IN ('PROGRAMADA', 'CONFIRMADA') " +
           "AND a.appointmentDate >= :today " +
           "ORDER BY a.appointmentDate ASC, a.startTime ASC")
    List<Appointment> findNextAppointmentByPatient(@Param("patientId") Long patientId,
                                                    @Param("today") LocalDate today);
}
