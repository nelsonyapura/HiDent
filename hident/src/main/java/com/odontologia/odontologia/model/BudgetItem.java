package com.odontologia.odontologia.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "budget_items")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    private Long idItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_budget", nullable = false)
    private Budget budget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_service")
    private DentalService service;

    @Column(name = "service_name", nullable = false, length = 200)
    private String serviceName;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "tooth", length = 20)
    private String tooth;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(name = "discount", nullable = false, precision = 5, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "PEN";

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        calculateSubtotal();
    }

    public void calculateSubtotal() {
        if (unitPrice != null && quantity != null) {
            BigDecimal base = unitPrice.multiply(BigDecimal.valueOf(quantity));
            if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discountFactor = BigDecimal.ONE.subtract(
                        discount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
                this.subtotal = base.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
            } else {
                this.subtotal = base.setScale(2, RoundingMode.HALF_UP);
            }
        }
    }

    @Transient
    public String getUnitPriceFormatted() {
        String symbol = "USD".equals(currency) ? "US$" : "S/";
        return symbol + " " + unitPrice.toPlainString();
    }

    @Transient
    public String getSubtotalFormatted() {
        String symbol = "USD".equals(currency) ? "US$" : "S/";
        return symbol + " " + subtotal.toPlainString();
    }

    @Transient
    public String getDiscountFormatted() {
        return discount != null && discount.compareTo(BigDecimal.ZERO) > 0
                ? discount.stripTrailingZeros().toPlainString() + "%"
                : "—";
    }
}
