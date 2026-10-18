package com.sathya.budget_app.service;

import com.sathya.budget_app.DTO.AlertPreferencesDTO;
import com.sathya.budget_app.DTO.BudgetUpdateDTO;
import com.sathya.budget_app.DTO.SavingsGoalDTO;
import com.sathya.budget_app.DTO.SettingsDTO;
import com.sathya.budget_app.Entity.AppSettings;
import com.sathya.budget_app.Repository.AppSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SettingsService {

    private static final Long SETTINGS_ID = 1L;

    @Autowired
    private AppSettingsRepository repository;

    // There's only ever one settings row (no multi-user support yet) —
    // create it with defaults the first time it's requested.
    private AppSettings loadOrCreate() {
        return repository.findById(SETTINGS_ID)
                .orElseGet(() -> {
                    AppSettings settings = new AppSettings();
                    settings.setId(SETTINGS_ID);
                    return repository.save(settings);
                });
    }

    public SettingsDTO getSettings() {
        return toDto(loadOrCreate());
    }

    public SettingsDTO updateMonthlyBudget(BudgetUpdateDTO dto) {
        AppSettings settings = loadOrCreate();
        settings.setMonthlyBudget(dto.monthlyBudget());
        return toDto(repository.save(settings));
    }

    // (Re)saving the goal restarts the countdown from today, so the
    // per-day/per-month pace the frontend calculates stays accurate.
    public SettingsDTO updateSavingsGoal(SavingsGoalDTO dto) {
        AppSettings settings = loadOrCreate();
        settings.setTargetSavings(dto.targetSavings());
        settings.setSavingsDurationMonths(dto.durationMonths());
        settings.setSavingsStartDate(LocalDate.now());
        return toDto(repository.save(settings));
    }

    // Settings > Alerts & Notifications toggles. Persisted immediately —
    // there's no separate "Save" button for this panel on the frontend.
    public SettingsDTO updateAlertPreferences(AlertPreferencesDTO dto) {
        AppSettings settings = loadOrCreate();
        settings.setBudgetAlert80Enabled(dto.budgetAlert80Enabled());
        settings.setDailyPaceRecapEnabled(dto.dailyPaceRecapEnabled());
        settings.setSavingsMilestonesEnabled(dto.savingsMilestonesEnabled());
        return toDto(repository.save(settings));
    }

    private SettingsDTO toDto(AppSettings settings) {
        SavingsGoalDTO goal = new SavingsGoalDTO(
                settings.getTargetSavings(),
                settings.getSavingsDurationMonths(),
                settings.getSavingsStartDate()
        );
        AlertPreferencesDTO alerts = new AlertPreferencesDTO(
                settings.getBudgetAlert80Enabled(),
                settings.getDailyPaceRecapEnabled(),
                settings.getSavingsMilestonesEnabled()
        );
        return new SettingsDTO(settings.getMonthlyBudget(), goal, alerts);
    }
}
