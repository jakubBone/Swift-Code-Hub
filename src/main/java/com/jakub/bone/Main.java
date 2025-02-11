import com.jakub.bone.database.Datasource;

import com.jakub.bone.utills.ExcelImporter;
import com.jakub.bone.domain.SwiftRecord;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        Datasource database = new Datasource();
        List<SwiftRecord> swiftCodeRecords = ExcelImporter.getExcelFile();
        database.getCodeRepository().insertSwiftRecords(swiftCodeRecords);
    }
}
