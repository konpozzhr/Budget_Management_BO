package com.sathya.budget_app.Repository;

import com.sathya.budget_app.Entity.CategoryBudget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryBudgetRepository extends JpaRepository<CategoryBudget, String> {
}
