package com.odontologia.odontologia.controller;

import com.odontologia.odontologia.model.Proforma;
import com.odontologia.odontologia.model.ProformaItem;
import com.odontologia.odontologia.model.User;
import com.odontologia.odontologia.service.ProformaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/proformas")
@RequiredArgsConstructor
public class ProformaApiController {

    private final ProformaService proformaService;

    @GetMapping
    public ResponseEntity<?> getAll(@AuthenticationPrincipal User user,
                                    @RequestParam(required = false) String search,
                                    @RequestParam(required = false) Long patientId) {
        if (user == null) return ResponseEntity.status(401).build();

        List<Proforma> list;
        if (patientId != null) {
            list = proformaService.findByPatient(patientId);
        } else if (search != null && !search.isBlank()) {
            list = proformaService.search(search.trim());
        } else {
            list = proformaService.findAll();
        }

        List<Map<String, Object>> result = list.stream()
                .map(this::proformaToListMap)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@AuthenticationPrincipal User user,
                                     @PathVariable Long id) {
        if (user == null) return ResponseEntity.status(401).build();
        try {
            Proforma p = proformaService.findByIdWithItems(id);
            return ResponseEntity.ok(proformaToDetailMap(p));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@AuthenticationPrincipal User user,
                                    @RequestBody Map<String, Object> body) {
        if (user == null) return ResponseEntity.status(401).build();
        try {
            String actor = user.getName() != null ? user.getName() : user.getUsername();

            Long patientId = Long.valueOf(body.get("patientId").toString());
            String proformaName = body.get("proformaName") != null ? body.get("proformaName").toString() : null;
            String doctorName = body.get("doctorName") != null ? body.get("doctorName").toString() : null;
            String notes = body.get("notes") != null ? body.get("notes").toString() : null;

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rawItems = (List<Map<String, Object>>) body.get("items");

            List<ProformaService.ItemRequest> items = rawItems.stream().map(m ->
                new ProformaService.ItemRequest(
                    m.get("serviceId") != null ? Long.valueOf(m.get("serviceId").toString()) : null,
                    m.get("serviceName") != null ? m.get("serviceName").toString() : "Sin nombre",
                    m.get("description") != null ? m.get("description").toString() : null,
                    m.get("price") != null ? new BigDecimal(m.get("price").toString()) : BigDecimal.ZERO,
                    m.get("currency") != null ? m.get("currency").toString() : "PEN"
                )
            ).collect(Collectors.toList());

            Proforma created = proformaService.createWithItems(
                    patientId, proformaName, doctorName, notes, actor, items);

            return ResponseEntity.ok(proformaToDetailMap(
                    proformaService.findByIdWithItems(created.getIdProforma())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<?> addItem(@AuthenticationPrincipal User user,
                                     @PathVariable Long id,
                                     @RequestBody Map<String, Object> body) {
        if (user == null) return ResponseEntity.status(401).build();
        try {
            Long serviceId = body.get("serviceId") != null ? Long.valueOf(body.get("serviceId").toString()) : null;
            String serviceName = body.get("serviceName") != null ? body.get("serviceName").toString() : "Sin nombre";
            String description = body.get("description") != null ? body.get("description").toString() : null;
            BigDecimal price = body.get("price") != null ? new BigDecimal(body.get("price").toString()) : BigDecimal.ZERO;
            String currency = body.get("currency") != null ? body.get("currency").toString() : "PEN";

            proformaService.addItem(id, serviceId, serviceName, description, price, currency);

            Proforma updated = proformaService.findByIdWithItems(id);
            return ResponseEntity.ok(proformaToDetailMap(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/items/{itemId}")
    public ResponseEntity<?> updateItem(@AuthenticationPrincipal User user,
                                        @PathVariable Long id,
                                        @PathVariable Long itemId,
                                        @RequestBody Map<String, Object> body) {
        if (user == null) return ResponseEntity.status(401).build();
        try {
            String serviceName = body.get("serviceName") != null ? body.get("serviceName").toString() : null;
            String description = body.get("description") != null ? body.get("description").toString() : null;
            BigDecimal price = body.get("price") != null ? new BigDecimal(body.get("price").toString()) : null;

            proformaService.updateItem(id, itemId, serviceName, description, price);

            Proforma updated = proformaService.findByIdWithItems(id);
            return ResponseEntity.ok(proformaToDetailMap(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<?> removeItem(@AuthenticationPrincipal User user,
                                        @PathVariable Long id,
                                        @PathVariable Long itemId) {
        if (user == null) return ResponseEntity.status(401).build();
        try {
            proformaService.removeItem(id, itemId);
            Proforma updated = proformaService.findByIdWithItems(id);
            return ResponseEntity.ok(proformaToDetailMap(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> markCompleted(@AuthenticationPrincipal User user,
                                           @PathVariable Long id,
                                           @RequestBody Map<String, Object> body) {
        if (user == null) return ResponseEntity.status(401).build();
        try {
            String actor = user.getName() != null ? user.getName() : user.getUsername();

            @SuppressWarnings("unchecked")
            List<Number> rawIds = (List<Number>) body.get("itemIds");
            List<Long> itemIds = rawIds.stream().map(Number::longValue).collect(Collectors.toList());

            proformaService.markItemsCompleted(id, itemIds, actor);

            Proforma updated = proformaService.findByIdWithItems(id);
            return ResponseEntity.ok(proformaToDetailMap(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(@AuthenticationPrincipal User user,
                                          @PathVariable Long id,
                                          @RequestBody Map<String, String> body) {
        if (user == null) return ResponseEntity.status(401).build();
        try {
            String actor = user.getName() != null ? user.getName() : user.getUsername();
            proformaService.changeStatus(id, body.get("status"), actor);
            return ResponseEntity.ok(Map.of("message", "Estado actualizado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal User user,
                                    @PathVariable Long id) {
        if (user == null) return ResponseEntity.status(401).build();
        try {
            proformaService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Proforma eliminada"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> proformaToListMap(Proforma p) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", p.getIdProforma());
        map.put("patientId", p.getPatient().getIdPatient());
        map.put("patientName", p.getPatient().getFullName());
        map.put("patientDni", p.getPatient().getDni());
        map.put("proformaName", p.getProformaName());
        map.put("doctorName", p.getDoctorName());
        map.put("total", p.getTotal());
        map.put("totalFormatted", p.getTotalFormatted());
        map.put("currency", p.getCurrency());
        map.put("status", p.getStatus());
        map.put("statusLabel", p.getStatusLabel());
        map.put("statusBadgeClass", p.getStatusBadgeClass());
        map.put("itemCount", p.getItemCount());
        map.put("completedCount", p.getCompletedCount());
        map.put("pendingCount", p.getPendingCount());
        map.put("createdAt", p.getCreatedAtFormatted());
        map.put("createdBy", p.getCreatedBy());
        return map;
    }

    private Map<String, Object> proformaToDetailMap(Proforma p) {
        Map<String, Object> map = proformaToListMap(p);
        map.put("notes", p.getNotes());
        map.put("updatedAt", p.getUpdatedAtFormatted());
        map.put("updatedBy", p.getUpdatedBy());

        List<Map<String, Object>> items = p.getItems().stream().map(i -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", i.getIdItem());
            m.put("serviceId", i.getServiceId());
            m.put("serviceName", i.getServiceName());
            m.put("description", i.getDescription());
            m.put("price", i.getPrice());
            m.put("priceFormatted", i.getPriceFormatted());
            m.put("currency", i.getCurrency());
            m.put("sortOrder", i.getSortOrder());
            m.put("completed", i.getCompleted());
            m.put("completedAt", i.getCompletedAtFormatted());
            m.put("completedBy", i.getCompletedBy());
            m.put("createdAt", i.getCreatedAtFormatted());
            return m;
        }).collect(Collectors.toList());

        map.put("items", items);
        return map;
    }
}
