package com.sathya.budget_app.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CategoryBudgetUpdateDTO(
        @NotNull(message = "Monthly cap is required")
        @DecimalMin(value = "0.0", message = "Monthly cap cannot be negative")
        BigDecimal monthlyCap
) {
}
