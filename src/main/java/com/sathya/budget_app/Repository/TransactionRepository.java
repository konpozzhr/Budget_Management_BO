package com.sathya.budget_app.Repository;

import com.sathya.budget_app.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // findAll(Sort) from JpaRepository covers the "sorted by date" listing
    // the frontend needs. Add custom queries here if you later move
    // aggregation (totals, category breakdowns, etc.) server-side.
}
