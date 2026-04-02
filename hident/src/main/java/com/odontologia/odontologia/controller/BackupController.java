package com.odontologia.odontologia.controller;

import com.odontologia.odontologia.model.User;
import com.odontologia.odontologia.service.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/backup")
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;

    @PostMapping
    public ResponseEntity<?> runBackup(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.status(401).build();

        String actor = user.getName() != null ? user.getName() : user.getUsername();

        BackupService.BackupResult result = backupService.runFullBackup(actor);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", result.success());
        response.put("filename", result.filename());
        response.put("sizeMB", result.sizeMB());
        response.put("message", result.message());

        return ResponseEntity.ok(response);
    }
}
