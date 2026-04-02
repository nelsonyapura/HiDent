package com.odontologia.odontologia.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proformas")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Proforma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proforma")
    private Long idProforma;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_patient", nullable = false)
    private Patient patient;

    @Column(name = "proforma_name", length = 200)
    private String proformaName;

    @Column(name = "doctor_name", length = 150)
    private String doctorName;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "PEN";

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVA";

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "proforma", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    private List<ProformaItem> items = new ArrayList<>();

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
    public String getStatusLabel() {
        return switch (status) {
            case "ACTIVA"     -> "Activa";
            case "COMPLETADA" -> "Completada";
            case "CANCELADA"  -> "Cancelada";
            default -> status;
        };
    }

    @Transient
    public String getStatusBadgeClass() {
        return switch (status) {
            case "ACTIVA"     -> "bg-success-subtle text-success";
            case "COMPLETADA" -> "bg-primary-subtle text-primary";
            case "CANCELADA"  -> "bg-danger-subtle text-danger";
            default -> "bg-secondary-subtle text-secondary";
        };
    }

    @Transient
    public String getTotalFormatted() {
        String symbol = "USD".equals(currency) ? "US$" : "S/";
        return symbol + " " + total.toPlainString();
    }

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transient
    public String getCreatedAtFormatted() {
        return createdAt != null ? createdAt.format(DT_FMT) : null;
    }

    @Transient
    public String getUpdatedAtFormatted() {
        return updatedAt != null ? updatedAt.format(DT_FMT) : null;
    }

    @Transient
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    @Transient
    public long getCompletedCount() {
        return items != null ? items.stream().filter(i -> Boolean.TRUE.equals(i.getCompleted())).count() : 0;
    }

    @Transient
    public long getPendingCount() {
        return getItemCount() - getCompletedCount();
    }

    public void recalculateTotal() {
        this.total = items.stream()
                .map(ProformaItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
