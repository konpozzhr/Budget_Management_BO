package com.sathya.budget_app.service;

import com.sathya.budget_app.DTO.TransactionDTO;
import com.sathya.budget_app.Entity.Transaction;
import com.sathya.budget_app.Repository.TransactionRepository;
import com.sathya.budget_app.exception.TransactionNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    public List<TransactionDTO> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "transactionDate"))
                .stream()
                .map(this::toDto)
                .toList();
    }

    public TransactionDTO findById(Long id) {
        return toDto(getEntityOrThrow(id));
    }

    public TransactionDTO create(TransactionDTO dto) {
        Transaction entity = new Transaction();
        applyDto(entity, dto);
        return toDto(repository.save(entity));
    }

    public TransactionDTO update(Long id, TransactionDTO dto) {
        Transaction entity = getEntityOrThrow(id);
        applyDto(entity, dto);
        return toDto(repository.save(entity));
    }

    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new TransactionNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private Transaction getEntityOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    private void applyDto(Transaction entity, TransactionDTO dto) {
        entity.setTransactionDate(dto.date());
        entity.setCategory(dto.category());
        entity.setDescription(dto.description());
        entity.setIncome(dto.income() != null ? dto.income() : BigDecimal.ZERO);
        entity.setOutgoing(dto.outgoing() != null ? dto.outgoing() : BigDecimal.ZERO);
    }

    private TransactionDTO toDto(Transaction entity) {
        return new TransactionDTO(
                entity.getId(),
                entity.getTransactionDate(),
                entity.getCategory(),
                entity.getDescription(),
                entity.getIncome(),
                entity.getOutgoing()
        );
    }

    public void exportExcel(HttpServletResponse response) throws IOException {

        List<Transaction> transactions =
                repository.findAll(Sort.by(Sort.Direction.DESC, "transactionDate"));

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");

        // ========= Styles =========

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        DataFormat dataFormat = workbook.createDataFormat();

        CellStyle moneyStyle = workbook.createCellStyle();
        moneyStyle.cloneStyleFrom(cellStyle);
        moneyStyle.setDataFormat(dataFormat.getFormat("$#,##0.00"));

        CellStyle incomeStyle = workbook.createCellStyle();
        incomeStyle.cloneStyleFrom(moneyStyle);
        Font incomeFont = workbook.createFont();
        incomeFont.setBold(true);
        incomeFont.setColor(IndexedColors.GREEN.getIndex());
        incomeStyle.setFont(incomeFont);

        CellStyle outgoingStyle = workbook.createCellStyle();
        outgoingStyle.cloneStyleFrom(moneyStyle);
        Font outgoingFont = workbook.createFont();
        outgoingFont.setBold(true);
        outgoingFont.setColor(IndexedColors.RED.getIndex());
        outgoingStyle.setFont(outgoingFont);

        // ========= Header =========

        Row header = sheet.createRow(0);
        String[] columns = {"Date", "Category", "Description", "Income", "Outgoing"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // ========= Data =========

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        int rowNum = 1;
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalOutgoing = BigDecimal.ZERO;

        for (Transaction trx : transactions) {

            Row row = sheet.createRow(rowNum++);

            Cell dateCell = row.createCell(0);
            dateCell.setCellValue(trx.getTransactionDate().format(formatter));
            dateCell.setCellStyle(cellStyle);

            Cell categoryCell = row.createCell(1);
            categoryCell.setCellValue(trx.getCategory());
            categoryCell.setCellStyle(cellStyle);

            Cell descCell = row.createCell(2);
            descCell.setCellValue(trx.getDescription());
            descCell.setCellStyle(cellStyle);

            Cell incomeCell = row.createCell(3);
            incomeCell.setCellValue(trx.getIncome().doubleValue());
            incomeCell.setCellStyle(trx.getIncome().compareTo(BigDecimal.ZERO) > 0 ? incomeStyle : moneyStyle);

            Cell outgoingCell = row.createCell(4);
            outgoingCell.setCellValue(trx.getOutgoing().doubleValue());
            outgoingCell.setCellStyle(trx.getOutgoing().compareTo(BigDecimal.ZERO) > 0 ? outgoingStyle : moneyStyle);

            totalIncome = totalIncome.add(trx.getIncome());
            totalOutgoing = totalOutgoing.add(trx.getOutgoing());
        }

        // ========= Summary =========

        BigDecimal balance = totalIncome.subtract(totalOutgoing);
        int summaryRow = rowNum + 2;

        CellStyle summaryLabelStyle = workbook.createCellStyle();
        summaryLabelStyle.cloneStyleFrom(headerStyle);
        summaryLabelStyle.setAlignment(HorizontalAlignment.RIGHT);

        Row incomeRow = sheet.createRow(summaryRow);
        Cell incomeLabel = incomeRow.createCell(2);
        incomeLabel.setCellValue("Total Income");
        incomeLabel.setCellStyle(summaryLabelStyle);
        Cell incomeValue = incomeRow.createCell(3);
        incomeValue.setCellValue(totalIncome.doubleValue());
        incomeValue.setCellStyle(incomeStyle);

        Row outgoingRow = sheet.createRow(summaryRow + 1);
        Cell outgoingLabel = outgoingRow.createCell(2);
        outgoingLabel.setCellValue("Total Outgoing");
        outgoingLabel.setCellStyle(summaryLabelStyle);
        Cell outgoingValue = outgoingRow.createCell(3);
        outgoingValue.setCellValue(totalOutgoing.doubleValue());
        outgoingValue.setCellStyle(outgoingStyle);

        Row balanceRow = sheet.createRow(summaryRow + 2);
        Cell balanceLabel = balanceRow.createCell(2);
        balanceLabel.setCellValue("Balance");
        balanceLabel.setCellStyle(summaryLabelStyle);
        Cell balanceValue = balanceRow.createCell(3);
        balanceValue.setCellValue(balance.doubleValue());

        CellStyle balanceStyle = workbook.createCellStyle();
        balanceStyle.cloneStyleFrom(moneyStyle);
        Font balanceFont = workbook.createFont();
        balanceFont.setBold(true);
        balanceFont.setColor(balance.compareTo(BigDecimal.ZERO) >= 0
                ? IndexedColors.GREEN.getIndex()
                : IndexedColors.RED.getIndex());
        balanceStyle.setFont(balanceFont);
        balanceValue.setCellStyle(balanceStyle);

        // ========= Excel features =========

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
        sheet.createFreezePane(0, 1);
        sheet.setAutoFilter(new CellRangeAddress(0, rowNum - 1, 0, columns.length - 1));

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
