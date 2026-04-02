package com.odontologia.odontologia.controller;

import com.odontologia.odontologia.dto.ToothUpdateRequest;
import com.odontologia.odontologia.model.OdontogramTooth;
import com.odontologia.odontologia.model.Patient;
import com.odontologia.odontologia.model.User;
import com.odontologia.odontologia.service.OdontogramService;
import com.odontologia.odontologia.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/odontogram")
public class OdontogramController {

    private final OdontogramService odontogramService;
    private final PatientService patientService;

    public OdontogramController(OdontogramService odontogramService,
                                 PatientService patientService) {
        this.odontogramService = odontogramService;
        this.patientService    = patientService;
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<List<OdontogramTooth>> getOdontogram(
            @AuthenticationPrincipal User user,
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "FDI") String nomenclature) {

        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(odontogramService.findByPatient(patientId, nomenclature));
    }

    @PostMapping("/{patientId}/tooth")
    public ResponseEntity<?> saveTooth(
            @AuthenticationPrincipal User user,
            @PathVariable Long patientId,
            @RequestBody ToothUpdateRequest req) {

        if (user == null) return ResponseEntity.status(401).build();

        Optional<Patient> patientOpt = patientService.findById(patientId);
        if (patientOpt.isEmpty()) return ResponseEntity.notFound().build();

        try {
            String actor = user.getName() != null ? user.getName() : user.getUsername();
            OdontogramTooth saved = odontogramService.saveTooth(patientOpt.get(), req, actor);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{patientId}/tooth/{toothCode}/condition/{condition}")
    public ResponseEntity<?> clearCondition(
            @AuthenticationPrincipal User user,
            @PathVariable Long patientId,
            @PathVariable String toothCode,
            @PathVariable String condition,
            @RequestParam(defaultValue = "FDI") String nomenclature) {

        if (user == null) return ResponseEntity.status(401).build();
        odontogramService.clearCondition(patientId, toothCode, nomenclature, condition);
        return ResponseEntity.ok(Map.of("message", "Condición eliminada"));
    }

    @DeleteMapping("/{patientId}/tooth/{toothCode}")
    public ResponseEntity<?> clearTooth(
            @AuthenticationPrincipal User user,
            @PathVariable Long patientId,
            @PathVariable String toothCode,
            @RequestParam(defaultValue = "FDI") String nomenclature) {

        if (user == null) return ResponseEntity.status(401).build();
        odontogramService.clearTooth(patientId, toothCode, nomenclature);
        return ResponseEntity.ok(Map.of("message", "Diente limpiado"));
    }
}
