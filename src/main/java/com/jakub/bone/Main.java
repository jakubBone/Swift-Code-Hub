import com.jakub.bone.database.Database;

import com.jakub.bone.utills.ExcelImporter;
import com.jakub.bone.utills.SwiftCode;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        Database database = new Database();
        List<SwiftCode> swiftCodes = ExcelImporter.getExcelFile();
        database.getCodeRepository().insertSwiftCodes(swiftCodes);
    }
}
