package com.sathya.budget_app.exception;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(Long id) {
        super("Transaction not found: id=" + id);
    }
}
