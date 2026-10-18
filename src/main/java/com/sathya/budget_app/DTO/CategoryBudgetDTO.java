package com.sathya.budget_app.DTO;

import java.math.BigDecimal;

public record CategoryBudgetDTO(
        String category,
        BigDecimal monthlyCap
) {
}
