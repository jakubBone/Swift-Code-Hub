package com.jakub.bone.utills;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelImporter {
    private static final String FILE_PATH = ConfigLoader.get("database.swift_codes");

    public static List<SwiftCode> getExcelFile() throws IOException {
        List<SwiftCode> swiftCodes = new ArrayList<>();

        FileInputStream fis = new FileInputStream(FILE_PATH);
        Workbook workbook = new HSSFWorkbook(fis);

        Sheet sheet = workbook.getSheetAt(0);

        for(Row row: sheet){
            if(row.getRowNum() == 0){
                continue;
            }
            String countryIso2 = row.getCell(0).getStringCellValue();
            String swiftCode = row.getCell(1).getStringCellValue();;
            String bankName = row.getCell(2).getStringCellValue();
            String address = row.getCell(3).getStringCellValue();
            String town = row.getCell(4).getStringCellValue();
            String country = row.getCell(5).getStringCellValue();

            swiftCodes.add(new SwiftCode(countryIso2, swiftCode, bankName,
                    address, town, country));
        }
        workbook.close();
        return swiftCodes;
    }

}
