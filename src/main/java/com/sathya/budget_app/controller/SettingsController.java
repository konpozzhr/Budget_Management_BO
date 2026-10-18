package com.sathya.budget_app.controller;

import com.sathya.budget_app.DTO.AlertPreferencesDTO;
import com.sathya.budget_app.DTO.BudgetUpdateDTO;
import com.sathya.budget_app.DTO.CategoryBudgetDTO;
import com.sathya.budget_app.DTO.CategoryBudgetUpdateDTO;
import com.sathya.budget_app.DTO.SavingsGoalDTO;
import com.sathya.budget_app.DTO.SettingsDTO;
import com.sathya.budget_app.service.CategoryBudgetService;
import com.sathya.budget_app.service.SettingsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private CategoryBudgetService categoryBudgetService;

    @GetMapping
    public SettingsDTO get() {
        return settingsService.getSettings();
    }

    @PutMapping("/budget")
    public SettingsDTO updateBudget(@Valid @RequestBody BudgetUpdateDTO dto) {
        return settingsService.updateMonthlyBudget(dto);
    }

    @PutMapping("/savings-goal")
    public SettingsDTO updateSavingsGoal(@Valid @RequestBody SavingsGoalDTO dto) {
        return settingsService.updateSavingsGoal(dto);
    }

    // Settings > Alerts & Notifications toggles (80% Budget Alert, Daily
    // Pace Recap, Savings Milestones). Saved immediately on each flip —
    // the frontend doesn't show a separate "Save" button for this card.
    @PutMapping("/alerts")
    public SettingsDTO updateAlertPreferences(@Valid @RequestBody AlertPreferencesDTO dto) {
        return settingsService.updateAlertPreferences(dto);
    }

    // { "Food & Drinks": 350.00, "Shopping": 150.00, ... } — a category with
    // no key just hasn't had a cap configured yet.
    @GetMapping("/category-budgets")
    public Map<String, BigDecimal> getCategoryBudgets() {
        return categoryBudgetService.getAll();
    }

    @PutMapping("/category-budgets/{category}")
    public CategoryBudgetDTO updateCategoryBudget(
            @PathVariable String category,
            @Valid @RequestBody CategoryBudgetUpdateDTO dto) {
        return categoryBudgetService.update(category, dto.monthlyCap());
    }
}
