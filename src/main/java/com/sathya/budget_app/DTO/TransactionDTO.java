package com.sathya.budget_app.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request/response shape for the transactions REST API. Deliberately mirrors
 * the shape the React app already works with (date/category/description/
 * income/outgoing) so the frontend needs no field-mapping logic.
 *
 * "id" is null on create requests (the server assigns it) and populated on
 * every response.
 */
public record TransactionDTO(
        Long id,

        @NotNull(message = "Date is required")
        LocalDate date,

        @NotBlank(message = "Category is required")
        String category,

        @NotBlank(message = "Description is required")
        @Size(max = 255, message = "Description is too long")
        String description,

        @NotNull(message = "Income is required")
        @DecimalMin(value = "0.0", message = "Income cannot be negative")
        BigDecimal income,

        @NotNull(message = "Outgoing is required")
        @DecimalMin(value = "0.0", message = "Outgoing cannot be negative")
        BigDecimal outgoing
) {
}
