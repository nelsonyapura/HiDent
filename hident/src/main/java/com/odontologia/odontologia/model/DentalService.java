package com.odontologia.odontologia.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "dental_services",
       uniqueConstraints = @UniqueConstraint(columnNames = {"category", "name"}))
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DentalService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_service")
    private Long idService;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "PEN";

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Transient
    public String getCategoryLabel() {
        return switch (category) {
            case "ODONTOLOGIA_GENERAL" -> "Odontología General";
            case "ORTODONCIA"          -> "Ortodoncia";
            case "EXTRACCION"          -> "Extracción";
            case "ENDODONCIA"          -> "Endodoncia";
            case "REHABILITACION"      -> "Rehabilitación";
            case "IMPLANTES"           -> "Implantes Dentales";
            case "PRODUCTOS"           -> "Productos";
            default -> category;
        };
    }

    @Transient
    public String getPriceFormatted() {
        String symbol = "USD".equals(currency) ? "US$" : "S/";
        return symbol + " " + unitPrice.toPlainString();
    }
}
