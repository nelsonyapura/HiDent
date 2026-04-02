package com.odontologia.odontologia.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class BudgetRequest {
    private Long patientId;
    private String budgetName;
    private String doctorName;
    private String patientNote;
    private String internalNote;
    private List<BudgetItemRequest> items = new ArrayList<>();

    @Getter @Setter
    public static class BudgetItemRequest {
        private Long serviceId;
        private String tooth;
        private Integer quantity = 1;
        private BigDecimal unitPrice;
        private BigDecimal discount;
        private String comment;
    }
}
