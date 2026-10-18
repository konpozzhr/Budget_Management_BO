package com.sathya.budget_app.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Single-row settings table (no multi-user support yet — always id = 1L,
 * see SettingsService for the get-or-create logic).
 *
 * Deliberately has no "currentSavings" column: the frontend treats current
 * savings as Net Savings (sum of income - sum of outgoing across all
 * transactions), computed on the fly rather than stored here.
 */
@Entity
@Table(name = "app_settings")
@Data
public class AppSettings {

    @Id
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyBudget = BigDecimal.valueOf(3800);

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal targetSavings = BigDecimal.valueOf(10000);

    @Column(nullable = false)
    private Integer savingsDurationMonths = 12;

    // The day the savings-goal countdown started — reset whenever the
    // goal is (re)saved, so per-day/per-month figures stay accurate.
    @Column(nullable = false)
    private LocalDate savingsStartDate = LocalDate.now();

    // Settings > Alerts & Notifications toggles. Defaults match the ones
    // the frontend ships with before a user ever touches this panel.
    // columnDefinition carries an explicit SQL default so Hibernate's
    // schema migration can backfill the existing settings row when these
    // columns are first added — without it, "ADD COLUMN ... NOT NULL" on
    // a table that already has data fails with NULL not allowed, which is
    // exactly the error from the log.
    @Column(nullable = false, columnDefinition = "boolean not null default true")
    private Boolean budgetAlert80Enabled = true;

    @Column(nullable = false, columnDefinition = "boolean not null default true")
    private Boolean dailyPaceRecapEnabled = true;

    @Column(nullable = false, columnDefinition = "boolean not null default false")
    private Boolean savingsMilestonesEnabled = false;
}
