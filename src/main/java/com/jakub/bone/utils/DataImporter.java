package com.jakub.bone.utils;

import com.jakub.bone.database.DataSource;
import com.jakub.bone.domain.SwiftRecord;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class DataImporter {
    private DataSource dataSource;

    public DataImporter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void importSwiftRecords() throws IOException, SQLException {
        String filePath = ConfigLoader.get("database.swift_codes");
        List<SwiftRecord> swiftRecords = importExcelFile(filePath);
        dataSource.getCodeRepository().insertSwiftRecords(swiftRecords);
        log.info("SWIFT Records imported successfully \n Records number: {}", swiftRecords.size());
    }

    private List<SwiftRecord> importExcelFile(String newPath) throws IOException {
        List<SwiftRecord> swiftCodes = new ArrayList<>();

        try(FileInputStream fis = new FileInputStream(newPath);
            Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                // Avoid first row (header)
                if (row.getRowNum() == 0) {
                    continue;
                }
                String countryIso2 = getCellValue(row.getCell(0));
                String swiftCode = getCellValue(row.getCell(1));
                String bankName = getCellValue(row.getCell(3));
                String address = getCellValue(row.getCell(4));
                String country = getCellValue(row.getCell(6));

                swiftCodes.add(new SwiftRecord(countryIso2, swiftCode, bankName,
                        address, country));
            }
        } catch(IOException ex){
            log.error("Failed to import file. Error: {}", ex.getMessage(), ex);
        }
        return swiftCodes;
    }

    // Method handling empty cells
    private static String getCellValue(Cell cell) {
        if (cell == null){
            return "";
        }
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue().trim() : "";
    }
}
