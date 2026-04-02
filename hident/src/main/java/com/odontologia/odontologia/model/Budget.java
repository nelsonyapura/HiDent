package com.odontologia.odontologia.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "budgets")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_budget")
    private Long idBudget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_patient", nullable = false)
    private Patient patient;

    @Column(name = "budget_name", length = 200)
    private String budgetName;

    @Column(name = "doctor_name", length = 150)
    private String doctorName;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "BORRADOR";

    @Column(name = "patient_note", columnDefinition = "TEXT")
    private String patientNote;

    @Column(name = "internal_note", columnDefinition = "TEXT")
    private String internalNote;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "PEN";

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    private List<BudgetItem> items = new ArrayList<>();

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
            case "BORRADOR"    -> "Borrador";
            case "ENVIADO"     -> "Enviado";
            case "ACEPTADO"    -> "Aceptado";
            case "EN_PROCESO"  -> "En proceso";
            case "COMPLETADO"  -> "Completado";
            case "RECHAZADO"   -> "Rechazado";
            default -> status;
        };
    }

    @Transient
    public String getStatusBadgeClass() {
        return switch (status) {
            case "BORRADOR"    -> "bg-secondary-subtle text-secondary";
            case "ENVIADO"     -> "bg-info-subtle text-info";
            case "ACEPTADO"    -> "bg-success-subtle text-success";
            case "EN_PROCESO"  -> "bg-warning-subtle text-warning";
            case "COMPLETADO"  -> "bg-primary-subtle text-primary";
            case "RECHAZADO"   -> "bg-danger-subtle text-danger";
            default -> "bg-secondary-subtle text-secondary";
        };
    }

    @Transient
    public String getTotalFormatted() {
        String symbol = "USD".equals(currency) ? "US$" : "S/";
        return symbol + " " + total.toPlainString();
    }

    @Transient
    public String getCreatedAtFormatted() {
        return createdAt != null ? createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : null;
    }

    @Transient
    public String getUpdatedAtFormatted() {
        return updatedAt != null ? updatedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : null;
    }

    @Transient
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void recalculateTotal() {
        this.total = items.stream()
                .map(BudgetItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
