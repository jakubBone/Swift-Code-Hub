package unit_tests;

import com.jakub.bone.database.DataSource;
import com.jakub.bone.domain.SwiftRecord;

import com.jakub.bone.utils.SwiftMapper;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SwiftCodeRepositoryTest {
    DataSource dataSource;

    @BeforeEach
    void setUp() throws SQLException {
        dataSource = new DataSource();
        dataSource.getDatabaseSchema().truncateTable();
    }

    @AfterEach
    void tearDown(){
        dataSource.getDatabaseSchema().truncateTable();
        dataSource.closeConnection();
    }

    @Test
    @DisplayName("Should test SWIFT Record create")
    void testCreateSwiftRecord() throws SQLException {
        SwiftRecord record = new SwiftRecord("PL", "ABCDEFXXX", "Bank1", "Address1", "POLAND");

        dataSource.getCodeRepository().createSwiftRecord(record);
        SwiftRecord result = dataSource.getCodeRepository().findBySwiftCode("ABCDEFXXX");

        assertEquals(record.getSwiftCode(), result.getSwiftCode());
    }

    @Test
    @DisplayName("Should test SWIFT Record find")
    void testFindRecordBySwiftCode() throws SQLException {
        SwiftRecord record = new SwiftRecord("PL", "ABCDEFXXX", "Bank1", "Address1", "POLAND");

        dataSource.getCodeRepository().createSwiftRecord(record);
        SwiftRecord result = dataSource.getCodeRepository().findBySwiftCode("ABCDEFXXX");

        assertEquals(record.getSwiftCode(), result.getSwiftCode());
        assertTrue(record.isHeadquarter());
    }

    @Test
    @DisplayName("Should test SWIFT Record delete")
    void testDeleteSwiftRecord() throws SQLException {
        SwiftRecord record = new SwiftRecord("PL", "ABCDEFXXX", "Bank1", "Address1", "POLAND");

        dataSource.getCodeRepository().createSwiftRecord(record);
        dataSource.getCodeRepository().deleteSwiftRecord(record.getSwiftCode());
        SwiftRecord result = dataSource.getCodeRepository().findBySwiftCode("ABCDEFXXX");

        assertNull(result);
    }

    @Test
    @DisplayName("Should test SWIFT Records find by countryISO2")
    void testFindAllByCountryIso2() throws SQLException {
        SwiftRecord record1 = new SwiftRecord("PL", "ABCDEFXXX", "Bank1", "Address1", "POLAND");
        SwiftRecord record2 = new SwiftRecord("PL", "ABCDEF111", "Bank2", "Address2", "POLAND");
        SwiftRecord record3 = new SwiftRecord("PL", "ABCDEF222", "Bank3", "Address3", "POLAND");

        dataSource.getCodeRepository().createSwiftRecord(record1);
        dataSource.getCodeRepository().createSwiftRecord(record2);
        dataSource.getCodeRepository().createSwiftRecord(record3);

        List<SwiftRecord> result = dataSource.getCodeRepository().findAllByCountryIso2("PL");

        assertEquals(result.get(0).getSwiftCode(),record1.getSwiftCode());
        assertEquals(result.get(1).getSwiftCode(),record2.getSwiftCode());
        assertEquals(result.get(2).getSwiftCode(),record3.getSwiftCode());
    }

    @Test
    @DisplayName("Should test SWIFT country name by countryISO2")
    void testFindCountryByISO2() throws SQLException {
        SwiftRecord record = new SwiftRecord("PL", "ABCDEFXXX", "Bank1", "Address1", "POLAND");

        dataSource.getCodeRepository().createSwiftRecord(record);
        String result = dataSource.getCodeRepository().findCountryByISO2("PL");

        assertEquals("POLAND",result);
    }

    @Test
    @DisplayName("Should test SWIFT Records insert")
    void testInsertSwiftRecords() throws SQLException {
        SwiftRecord record1 = new SwiftRecord("PL", "ABCDEFXXX", "Bank1", "Address1", "POLAND");
        SwiftRecord record2 = new SwiftRecord("PL", "ABCDEF111", "Bank2", "Address2", "POLAND");
        SwiftRecord record3 = new SwiftRecord("PL", "ABCDEF222", "Bank3", "Address3", "POLAND");

        List<SwiftRecord> testRecords = List.of(record1, record2, record3);

        dataSource.getCodeRepository().insertSwiftRecords(testRecords);

        SwiftRecord result1 = dataSource.getCodeRepository().findBySwiftCode("ABCDEFXXX");
        SwiftRecord result2 = dataSource.getCodeRepository().findBySwiftCode("ABCDEF111");
        SwiftRecord result3 = dataSource.getCodeRepository().findBySwiftCode("ABCDEF222");


        assertEquals(record1.getSwiftCode(), result1.getSwiftCode());
        assertEquals(record2.getSwiftCode(), result2.getSwiftCode());
        assertEquals(record3.getSwiftCode(), result3.getSwiftCode());
        assertTrue(record1.isHeadquarter());
        assertFalse(record2.isHeadquarter());
        assertFalse(record3.isHeadquarter());
    }

    @Test
    @DisplayName("Should test SWIFT Records find")
    void testFindAllBranchesByHeadquarter() throws SQLException {
        SwiftRecord hqRecord = new SwiftRecord("PL", "ABCDEFGHXXX", "Bank1", "Address1", "POLAND");
        SwiftRecord branchRecord1 = new SwiftRecord("PL", "ABCDEFGH111", "Bank2", "Address2", "POLAND");
        SwiftRecord branchRecord2 = new SwiftRecord("PL", "ABCDEFGH222", "Bank3", "Address3", "POLAND");

        dataSource.getCodeRepository().createSwiftRecord(hqRecord);
        dataSource.getCodeRepository().createSwiftRecord(branchRecord1);
        dataSource.getCodeRepository().createSwiftRecord(branchRecord2);

        List<SwiftRecord> testBranches = dataSource.getCodeRepository().findAllBranchesByHeadquarter(hqRecord.getSwiftCode());
        Map<String, Object> testMap = SwiftMapper.mapHeadquarterSwiftRecordWithBranches(hqRecord, testBranches);

        assertTrue(testMap.containsKey("branches"));
        List<Map<String, Object>> branchList = (List<Map<String, Object>>) testMap.get("branches");
        assertEquals(2, branchList.size());
    }
}
