package com.sathya.budget_app.controller;

import com.sathya.budget_app.DTO.TransactionDTO;
import com.sathya.budget_app.service.TransactionService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public List<TransactionDTO> getAll() {
        return transactionService.findAll();
    }

    @GetMapping("/{id}")
    public TransactionDTO getById(@PathVariable Long id) {
        return transactionService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDTO create(@Valid @RequestBody TransactionDTO dto) {
        return transactionService.create(dto);
    }

    @PutMapping("/{id}")
    public TransactionDTO update(@PathVariable Long id, @Valid @RequestBody TransactionDTO dto) {
        return transactionService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response) throws IOException {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "BudgetPro_Transactions_" + dateTime + ".xlsx";

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        transactionService.exportExcel(response);
    }
}
