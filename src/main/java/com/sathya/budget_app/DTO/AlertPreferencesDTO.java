package com.sathya.budget_app.DTO;

import jakarta.validation.constraints.NotNull;

/**
 * Alert/notification toggle preferences for the Settings > Alerts &
 * Notifications panel. Booleans only — there's no "unset" state, so
 * @NotNull is enough validation (guards against a missing field in the
 * request body rather than an explicit null).
 */
public record AlertPreferencesDTO(
        @NotNull(message = "budgetAlert80Enabled is required")
        Boolean budgetAlert80Enabled,

        @NotNull(message = "dailyPaceRecapEnabled is required")
        Boolean dailyPaceRecapEnabled,

        @NotNull(message = "savingsMilestonesEnabled is required")
        Boolean savingsMilestonesEnabled
) {
}
