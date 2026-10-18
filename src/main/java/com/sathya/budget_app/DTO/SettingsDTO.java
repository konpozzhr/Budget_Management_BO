package com.sathya.budget_app.DTO;

import java.math.BigDecimal;

public record SettingsDTO(
        BigDecimal monthlyBudget,
        SavingsGoalDTO savingsGoal,
        AlertPreferencesDTO alertPreferences
) {
}
