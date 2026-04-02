package com.odontologia.odontologia.controller;

import com.odontologia.odontologia.dto.AppointmentRequest;
import com.odontologia.odontologia.model.Appointment;
import com.odontologia.odontologia.model.Patient;
import com.odontologia.odontologia.model.User;
import com.odontologia.odontologia.service.AppointmentService;
import com.odontologia.odontologia.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentApiController {

    private final AppointmentService appointmentService;
    private final PatientService patientService;

    public AppointmentApiController(AppointmentService appointmentService,
            PatientService patientService) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAppointments(
            @AuthenticationPrincipal User user,
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(defaultValue = "TODOS") String status) {

        if (user == null)
            return ResponseEntity.status(401).build();

        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        List<Appointment> appointments = appointmentService.findByDateRangeAndStatus(startDate, endDate, status);

        List<Map<String, Object>> result = appointments.stream().map(a -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", a.getIdAppointment());
            map.put("patientId", a.getPatient().getIdPatient());
            map.put("patientName", a.getPatient().getFullName());
            map.put("patientDni", a.getPatient().getDni());
            map.put("date", a.getAppointmentDate().toString());
            map.put("startTime", a.getStartTime().toString());
            map.put("endTime", a.getEndTime() != null ? a.getEndTime().toString() : null);
            map.put("startFormatted", a.getStartTimeFormatted());
            map.put("endFormatted", a.getEndTimeFormatted());
            map.put("reason", a.getReason());
            map.put("notes", a.getNotes());
            map.put("status", a.getStatus());
            map.put("statusLabel", a.getStatusLabel());
            map.put("assignedTo", a.getAssignedTo());
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointment(@AuthenticationPrincipal User user,
            @PathVariable Long id) {
        if (user == null)
            return ResponseEntity.status(401).build();

        Optional<Appointment> opt = appointmentService.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.notFound().build();

        Appointment a = opt.get();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", a.getIdAppointment());
        map.put("patientId", a.getPatient().getIdPatient());
        map.put("patientName", a.getPatient().getFullName());
        map.put("patientDni", a.getPatient().getDni());
        map.put("date", a.getAppointmentDate().toString());
        map.put("startTime", a.getStartTime().toString());
        map.put("endTime", a.getEndTime() != null ? a.getEndTime().toString() : null);
        map.put("reason", a.getReason());
        map.put("notes", a.getNotes());
        map.put("status", a.getStatus());
        map.put("statusLabel", a.getStatusLabel());
        map.put("assignedTo", a.getAssignedTo());
        return ResponseEntity.ok(map);
    }

    @PostMapping
    public ResponseEntity<?> createAppointment(@AuthenticationPrincipal User user,
            @RequestBody AppointmentRequest req) {
        if (user == null)
            return ResponseEntity.status(401).build();

        try {
            Patient patient = patientService.findById(req.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

            String actor = user.getName() != null ? user.getName() : user.getUsername();

            Appointment saved = appointmentService.create(
                    patient,
                    LocalDate.parse(req.getAppointmentDate()),
                    LocalTime.parse(req.getStartTime()),
                    LocalTime.parse(req.getEndTime()),
                    req.getReason(),
                    req.getNotes(),
                    req.getAssignedTo(),
                    actor);

            return ResponseEntity.ok(Map.of(
                    "id", saved.getIdAppointment(),
                    "message", "Cita creada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(@AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody AppointmentRequest req) {
        if (user == null)
            return ResponseEntity.status(401).build();

        try {
            String actor = user.getName() != null ? user.getName() : user.getUsername();

            appointmentService.update(
                    id,
                    LocalDate.parse(req.getAppointmentDate()),
                    LocalTime.parse(req.getStartTime()),
                    LocalTime.parse(req.getEndTime()),
                    req.getReason(),
                    req.getNotes(),
                    req.getAssignedTo(),
                    actor);

            return ResponseEntity.ok(Map.of("message", "Cita actualizada"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(@AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        if (user == null)
            return ResponseEntity.status(401).build();

        try {
            String actor = user.getName() != null ? user.getName() : user.getUsername();
            String newStatus = body.get("status");

            appointmentService.changeStatus(id, newStatus, actor);
            return ResponseEntity.ok(Map.of("message", "Estado actualizado a " + newStatus));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@AuthenticationPrincipal User user,
            @PathVariable Long id) {
        if (user == null)
            return ResponseEntity.status(401).build();

        try {
            appointmentService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Cita eliminada"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/professionals")
    public ResponseEntity<List<String>> getProfessionals(@AuthenticationPrincipal User user) {
        if (user == null)
            return ResponseEntity.status(401).build();
        return ResponseEntity.ok(appointmentService.findDistinctProfessionals());
    }

    @GetMapping("/patients/search")
    public ResponseEntity<List<Map<String, Object>>> searchPatients(
            @AuthenticationPrincipal User user,
            @RequestParam("q") String query) {

        if (user == null)
            return ResponseEntity.status(401).build();

        List<Map<String, Object>> result = patientService.search(query).stream()
                .limit(10)
                .map(p -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id", p.getIdPatient());
                    map.put("name", p.getFullName());
                    map.put("dni", p.getDni());
                    return map;
                }).toList();

        return ResponseEntity.ok(result);
    }
}
