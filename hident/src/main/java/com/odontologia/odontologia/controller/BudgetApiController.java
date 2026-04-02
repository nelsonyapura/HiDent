package com.odontologia.odontologia.controller;

import com.odontologia.odontologia.dto.BudgetRequest;
import com.odontologia.odontologia.model.Budget;
import com.odontologia.odontologia.model.BudgetItem;
import com.odontologia.odontologia.model.User;
import com.odontologia.odontologia.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetApiController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<?> create(@AuthenticationPrincipal User user,
                                    @RequestBody BudgetRequest request) {
        if (user == null) return ResponseEntity.status(401).build();
        try {
            String username = user.getName() != null ? user.getName() : user.getUsername();

            List<BudgetService.ItemRequest> items = request.getItems().stream()
                    .map(i -> new BudgetService.ItemRequest(
                            i.getServiceId(), i.getTooth(), i.getQuantity(),
                            i.getUnitPrice(), i.getDiscount(), i.getComment()))
                    .collect(Collectors.toList());

            Budget budget = budgetService.createWithItems(
                    request.getPatientId(), request.getBudgetName(),
                    request.getDoctorName(), request.getPatientNote(),
                    request.getInternalNote(), username, items);

            return ResponseEntity.ok(budgetToMap(budget));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@AuthenticationPrincipal User user,
                                     @PathVariable Long id) {
        if (user == null) return ResponseEntity.status(401).build();
        try {
            Budget budget = budgetService.findByIdWithItems(id);
            return ResponseEntity.ok(budgetToMap(budget));
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
            Long serviceId = Long.valueOf(body.get("serviceId").toString());
            String tooth = body.get("tooth") != null ? body.get("tooth").toString() : null;
            Integer quantity = body.get("quantity") != null ?
                    Integer.valueOf(body.get("quantity").toString()) : 1;
            BigDecimal unitPrice = body.get("unitPrice") != null ?
                    new BigDecimal(body.get("unitPrice").toString()) : null;
            BigDecimal discount = body.get("discount") != null ?
                    new BigDecimal(body.get("discount").toString()) : BigDecimal.ZERO;
            String comment = body.get("comment") != null ? body.get("comment").toString() : null;

            BudgetItem item = budgetService.addItem(id, serviceId, tooth,
                    quantity, unitPrice, discount, comment);

            Budget budget = budgetService.findByIdWithItems(id);
            return ResponseEntity.ok(budgetToMap(budget));
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
            String tooth = body.get("tooth") != null ? body.get("tooth").toString() : null;
            Integer quantity = body.get("quantity") != null ?
                    Integer.valueOf(body.get("quantity").toString()) : null;
            BigDecimal unitPrice = body.get("unitPrice") != null ?
                    new BigDecimal(body.get("unitPrice").toString()) : null;
            BigDecimal discount = body.get("discount") != null ?
                    new BigDecimal(body.get("discount").toString()) : null;
            String comment = body.get("comment") != null ? body.get("comment").toString() : null;

            budgetService.updateItem(id, itemId, tooth, quantity, unitPrice, discount, comment);

            Budget budget = budgetService.findByIdWithItems(id);
            return ResponseEntity.ok(budgetToMap(budget));
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
            budgetService.removeItem(id, itemId);
            Budget budget = budgetService.findByIdWithItems(id);
            return ResponseEntity.ok(budgetToMap(budget));
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
            String username = user.getName() != null ? user.getName() : user.getUsername();
            Budget budget = budgetService.changeStatus(id, body.get("status"), username);
            return ResponseEntity.ok(Map.of("id", budget.getIdBudget(),
                    "status", budget.getStatus(), "statusLabel", budget.getStatusLabel()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInfo(@AuthenticationPrincipal User user,
                                        @PathVariable Long id,
                                        @RequestBody Map<String, String> body) {
        if (user == null) return ResponseEntity.status(401).build();
        try {
            String username = user.getName() != null ? user.getName() : user.getUsername();
            Budget budget = budgetService.updateInfo(id,
                    body.get("budgetName"), body.get("doctorName"),
                    body.get("patientNote"), body.get("internalNote"), username);
            return ResponseEntity.ok(budgetToMap(budget));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal User user,
                                    @PathVariable Long id) {
        if (user == null) return ResponseEntity.status(401).build();
        try {
            budgetService.delete(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> budgetToMap(Budget b) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", b.getIdBudget());
        map.put("patientId", b.getPatient().getIdPatient());
        map.put("patientName", b.getPatient().getFullName());
        map.put("patientDni", b.getPatient().getDni());
        map.put("budgetName", b.getBudgetName());
        map.put("doctorName", b.getDoctorName());
        map.put("status", b.getStatus());
        map.put("statusLabel", b.getStatusLabel());
        map.put("total", b.getTotal());
        map.put("totalFormatted", b.getTotalFormatted());
        map.put("currency", b.getCurrency());
        map.put("patientNote", b.getPatientNote());
        map.put("internalNote", b.getInternalNote());
        map.put("createdAt", b.getCreatedAtFormatted());
        map.put("updatedAt", b.getUpdatedAtFormatted());
        map.put("createdBy", b.getCreatedBy());

        if (b.getItems() != null) {
            map.put("items", b.getItems().stream().map(this::itemToMap).collect(Collectors.toList()));
        }
        return map;
    }

    private Map<String, Object> itemToMap(BudgetItem i) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", i.getIdItem());
        map.put("serviceId", i.getService() != null ? i.getService().getIdService() : null);
        map.put("serviceName", i.getServiceName());
        map.put("category", i.getCategory());
        map.put("tooth", i.getTooth());
        map.put("quantity", i.getQuantity());
        map.put("unitPrice", i.getUnitPrice());
        map.put("unitPriceFormatted", i.getUnitPriceFormatted());
        map.put("discount", i.getDiscount());
        map.put("discountFormatted", i.getDiscountFormatted());
        map.put("subtotal", i.getSubtotal());
        map.put("subtotalFormatted", i.getSubtotalFormatted());
        map.put("currency", i.getCurrency());
        map.put("comment", i.getComment());
        return map;
    }
}
