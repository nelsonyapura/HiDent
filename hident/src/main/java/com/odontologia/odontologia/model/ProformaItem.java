package com.odontologia.odontologia.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "proforma_items")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProformaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    private Long idItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proforma", nullable = false)
    private Proforma proforma;

    @Column(name = "id_service")
    private Long serviceId;

    @Column(name = "service_name", nullable = false, length = 200)
    private String serviceName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "PEN";

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "completed")
    private Boolean completed = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "completed_by", length = 100)
    private String completedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Transient
    public String getPriceFormatted() {
        String symbol = "USD".equals(currency) ? "US$" : "S/";
        return symbol + " " + price.toPlainString();
    }

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transient
    public String getCompletedAtFormatted() {
        return completedAt != null ? completedAt.format(DT_FMT) : null;
    }

    @Transient
    public String getCreatedAtFormatted() {
        return createdAt != null ? createdAt.format(DT_FMT) : null;
    }

    public void markCompleted(String actor) {
        this.completed = true;
        this.completedAt = LocalDateTime.now();
        this.completedBy = actor;
    }
}
