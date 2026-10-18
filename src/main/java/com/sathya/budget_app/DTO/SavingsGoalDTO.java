package com.sathya.budget_app.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Savings-goal shape for the settings API. No "currentSavings" field on
 * purpose — the frontend derives that from Net Savings (income - outgoing
 * across all transactions) rather than storing it separately.
 *
 * "startDate" is read-only on the way out (the server sets it whenever the
 * goal is saved) and ignored if sent on a request.
 */
public record SavingsGoalDTO(
        @NotNull(message = "Target savings is required")
        @DecimalMin(value = "0.0", message = "Target savings cannot be negative")
        BigDecimal targetSavings,

        @NotNull(message = "Duration is required")
        @Min(value = 1, message = "Duration must be at least 1 month")
        Integer durationMonths,

        LocalDate startDate
) {
}
