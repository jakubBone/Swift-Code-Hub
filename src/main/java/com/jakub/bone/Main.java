import com.jakub.bone.database.Datasource;

import com.jakub.bone.utills.ConfigLoader;
import com.jakub.bone.utills.FileImporter;
import com.jakub.bone.domain.SwiftRecord;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        String file_path = ConfigLoader.get("database.swift_codes");
        Datasource database = new Datasource();


        List<SwiftRecord> swiftCodeRecords = FileImporter.importExcelFile(file_path);
        database.getCodeRepository().insertSwiftRecords(swiftCodeRecords);
    }
}
