package com.jakub.bone.utills;

import com.jakub.bone.domain.SwiftRecord;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ExcelImporter {
    private static final String file_path = ConfigLoader.get("database.swift_codes");

    public static List<SwiftRecord> getExcelFile() throws IOException {
        List<SwiftRecord> swiftCodes = new ArrayList<>();

        try(FileInputStream fis = new FileInputStream(file_path);
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
                String town = getCellValue(row.getCell(5));
                String country = getCellValue(row.getCell(6));

                swiftCodes.add(new SwiftRecord(countryIso2, swiftCode, bankName,
                        address, town, country));
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
