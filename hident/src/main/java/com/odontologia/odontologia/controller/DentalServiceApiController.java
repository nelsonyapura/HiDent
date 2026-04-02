package com.odontologia.odontologia.controller;

import com.odontologia.odontologia.model.DentalService;
import com.odontologia.odontologia.model.User;
import com.odontologia.odontologia.service.DentalServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class DentalServiceApiController {

    private final DentalServiceService serviceService;

    @GetMapping
    public ResponseEntity<?> getAllGrouped(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.status(401).build();

        Map<String, List<DentalService>> grouped = serviceService.findAllGroupedByCategory();

        List<Map<String, Object>> result = new ArrayList<>();
        grouped.forEach((category, services) -> {
            Map<String, Object> group = new LinkedHashMap<>();
            group.put("category", category);
            group.put("categoryLabel", services.isEmpty() ? category :
                    services.get(0).getCategoryLabel());
            group.put("services", services.stream().map(this::toMap).collect(Collectors.toList()));
            result.add(group);
        });

        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@AuthenticationPrincipal User user,
                                    @RequestParam("q") String term) {
        if (user == null) return ResponseEntity.status(401).build();
        List<DentalService> results = serviceService.search(term);
        return ResponseEntity.ok(results.stream().map(this::toMap).collect(Collectors.toList()));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> byCategory(@AuthenticationPrincipal User user,
                                        @PathVariable String category) {
        if (user == null) return ResponseEntity.status(401).build();
        List<DentalService> results = serviceService.findByCategory(category);
        return ResponseEntity.ok(results.stream().map(this::toMap).collect(Collectors.toList()));
    }

    @PutMapping("/{id}/price")
    public ResponseEntity<?> updatePrice(@AuthenticationPrincipal User user,
                                         @PathVariable Long id,
                                         @RequestBody Map<String, Object> body) {
        if (user == null) return ResponseEntity.status(401).build();
        try {
            BigDecimal newPrice = new BigDecimal(body.get("unitPrice").toString());
            DentalService updated = serviceService.updatePrice(id, newPrice);
            return ResponseEntity.ok(toMap(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@AuthenticationPrincipal User user,
                                    @PathVariable Long id,
                                    @RequestBody Map<String, Object> body) {
        if (user == null) return ResponseEntity.status(401).build();
        try {
            String name = body.get("name") != null ? body.get("name").toString() : null;
            BigDecimal price = body.get("unitPrice") != null ?
                    new BigDecimal(body.get("unitPrice").toString()) : null;
            String currency = body.get("currency") != null ? body.get("currency").toString() : null;

            DentalService updated = serviceService.update(id, name, price, currency);
            return ResponseEntity.ok(toMap(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@AuthenticationPrincipal User user,
                                    @RequestBody Map<String, Object> body) {
        if (user == null) return ResponseEntity.status(401).build();
        try {
            String category = body.get("category").toString();
            String name = body.get("name").toString();
            BigDecimal price = new BigDecimal(body.get("unitPrice").toString());
            String currency = body.get("currency") != null ? body.get("currency").toString() : "PEN";

            DentalService created = serviceService.create(category, name, price, currency);
            return ResponseEntity.ok(toMap(created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deactivate(@AuthenticationPrincipal User user,
                                        @PathVariable Long id) {
        if (user == null) return ResponseEntity.status(401).build();
        try {
            serviceService.deactivate(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> toMap(DentalService s) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", s.getIdService());
        map.put("category", s.getCategory());
        map.put("categoryLabel", s.getCategoryLabel());
        map.put("name", s.getName());
        map.put("unitPrice", s.getUnitPrice());
        map.put("currency", s.getCurrency());
        map.put("priceFormatted", s.getPriceFormatted());
        map.put("sortOrder", s.getSortOrder());
        return map;
    }
}
