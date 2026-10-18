package com.sathya.budget_app.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * One monthly spending cap for one category, e.g. ("Food & Drinks", 350.00).
 * A category with no row here just hasn't had a cap configured yet — the
 * frontend treats that as "no budget set", not a zero cap.
 */
@Entity
@Table(name = "category_budgets")
@Data
public class CategoryBudget {

    @Id
    private String category;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyCap = BigDecimal.ZERO;
}
