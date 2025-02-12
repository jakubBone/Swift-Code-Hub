package unit_tests;

import com.jakub.bone.database.Datasource;
import com.jakub.bone.domain.SwiftRecord;

import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SwiftCodeRepositoryTest {
    Datasource datasource;

    @BeforeEach
    void setUp() throws SQLException {
        datasource = new Datasource();
        datasource.getDatabaseSchema().clearTables();
    }

    @AfterEach
    void tearDown(){
        datasource.getDatabaseSchema().clearTables();
        datasource.closeConnection();
    }

    @Test
    @DisplayName("Should test SWIFT records create")
    void testCreateSwiftRecord() throws SQLException {
        SwiftRecord record = new SwiftRecord("PL", "ABCDEFXXX", "Bank1", "Address1", "POLAND");

        datasource.getCodeRepository().createSwiftRecord(record);
        SwiftRecord result = datasource.getCodeRepository().findBySwiftCode("ABCDEFXXX");

        assertEquals(record.getSwiftCode(), result.getSwiftCode());
    }

    @Test
    @DisplayName("Should test SWIFT records find")
    void testFindBySwiftCode() throws SQLException {
        SwiftRecord record = new SwiftRecord("PL", "ABCDEFXXX", "Bank1", "Address1", "POLAND");
        datasource.getCodeRepository().createSwiftRecord(record);

        SwiftRecord result = datasource.getCodeRepository().findBySwiftCode("ABCDEFXXX");

        assertEquals(record.getSwiftCode(), result.getSwiftCode());
        assertTrue(record.isHeadquarter());
    }

    @Test
    @DisplayName("Should test SWIFT records insert")
    void testInsertSwiftRecords() throws SQLException {
        SwiftRecord record1 = new SwiftRecord("PL", "ABCDEFXXX", "Bank1", "Address1", "POLAND");
        SwiftRecord record2 = new SwiftRecord("PL", "ABCDEF111", "Bank2", "Address2", "POLAND");
        SwiftRecord record3 = new SwiftRecord("PL", "ABCDEF222", "Bank3", "Address3", "POLAND");

        List<SwiftRecord> testRecords = List.of(record1, record2, record3);

        datasource.getCodeRepository().insertSwiftRecords(testRecords);

        SwiftRecord result1 = datasource.getCodeRepository().findBySwiftCode("ABCDEFXXX");
        SwiftRecord result2 = datasource.getCodeRepository().findBySwiftCode("ABCDEF111");
        SwiftRecord result3 = datasource.getCodeRepository().findBySwiftCode("ABCDEF222");


        assertEquals(record1.getSwiftCode(), result1.getSwiftCode());
        assertEquals(record2.getSwiftCode(), result2.getSwiftCode());
        assertEquals(record3.getSwiftCode(), result3.getSwiftCode());
        assertTrue(record1.isHeadquarter());
        assertFalse(record2.isHeadquarter());
        assertFalse(record3.isHeadquarter());
    }

}
