package com.sathya.budget_app.service;

import com.sathya.budget_app.DTO.CategoryBudgetDTO;
import com.sathya.budget_app.Entity.CategoryBudget;
import com.sathya.budget_app.Repository.CategoryBudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CategoryBudgetService {

    @Autowired
    private CategoryBudgetRepository repository;

    // { "Food & Drinks": 350.00, "Shopping": 150.00, ... }. Categories with
    // no cap configured simply have no entry — the frontend treats that as
    // "no budget set" rather than a zero cap.
    public Map<String, BigDecimal> getAll() {
        Map<String, BigDecimal> caps = new LinkedHashMap<>();
        repository.findAll().forEach(cb -> caps.put(cb.getCategory(), cb.getMonthlyCap()));
        return caps;
    }

    public CategoryBudgetDTO update(String category, BigDecimal monthlyCap) {
        CategoryBudget budget = repository.findById(category).orElseGet(CategoryBudget::new);
        budget.setCategory(category);
        budget.setMonthlyCap(monthlyCap);
        CategoryBudget saved = repository.save(budget);
        return new CategoryBudgetDTO(saved.getCategory(), saved.getMonthlyCap());
    }
}
