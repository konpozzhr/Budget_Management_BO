package com.sathya.budget_app.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate transactionDate;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(nullable = false, length = 50)
    private String category;

    // A row can carry either an income or an outgoing amount (matches the
    // frontend's TransactionForm, which has both fields on every entry).
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal income = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal outgoing = BigDecimal.ZERO;

    public Transaction() {
    }
}
