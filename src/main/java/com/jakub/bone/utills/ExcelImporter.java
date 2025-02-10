package com.jakub.bone.utills;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelImporter {
    private static final String FILE_PATH = ConfigLoader.get("database.swift_codes");

    public static List<SwiftCode> getExcelFile() throws IOException {
        List<SwiftCode> swiftCodes = new ArrayList<>();

        FileInputStream fis = new FileInputStream(FILE_PATH);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        for(Row row: sheet){
            if(row.getRowNum() == 0){
                continue;
            }
            String countryIso2 = getCellValue(row.getCell(0));
            String swiftCode = getCellValue(row.getCell(1));
            String bankName = getCellValue(row.getCell(3));
            String address = getCellValue(row.getCell(4));
            String town = getCellValue(row.getCell(5));
            String country = getCellValue(row.getCell(6));

            swiftCodes.add(new SwiftCode(countryIso2, swiftCode, bankName,
                    address, town, country));
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
