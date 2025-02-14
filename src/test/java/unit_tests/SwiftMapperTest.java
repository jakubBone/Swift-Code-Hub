package unit_tests;

import com.jakub.bone.domain.SwiftRecord;
import com.jakub.bone.utills.SwiftMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SwiftMapperTest {
    @Test
    @DisplayName("Should test country SWIFT Records mapping correctness")
    void testMapCountrySwiftRecords(){
        SwiftRecord record1 = new SwiftRecord("PL", "ABCDEF111", "Bank1", "Address1", "POLAND");
        SwiftRecord record2 = new SwiftRecord("PL", "ABCDEF222", "Bank2", "Address2", "POLAND");
        List<SwiftRecord> swiftRecords =  List.of(record1, record2);

        Map<String, Object> testMap = SwiftMapper.mapCountrySwiftRecords("PL", "POLAND", swiftRecords);

        assertEquals("PL", testMap.get("countryISO2"));
        assertEquals("POLAND", testMap.get("countryName"));
        assertNotEquals("ES", testMap.get("countryISO2"));
        assertNotEquals("SPAIN", testMap.get("countryName"));

        assertTrue(testMap.containsKey("swiftCodes"));
        List<Map<String, Object>> swiftCodes = (List<Map<String, Object>>) testMap.get("swiftCodes");
        assertEquals(2, swiftCodes.size());
    }

    @Test
    @DisplayName("Should test headquarter SWIFT Record mapping correctness")
    void testMapHeadquarterSwiftRecordWithBranches(){
        SwiftRecord hqRecord = new SwiftRecord("PL", "ABCDEFXXX", "Bank1", "Address1", "POLAND");
        SwiftRecord branchRecord1 = new SwiftRecord("PL", "ABCDEF111", "Bank2", "Address2", "POLAND");
        SwiftRecord branchRecord2 = new SwiftRecord("PL", "ABCDEF222", "Bank3", "Address3", "POLAND");
        List<SwiftRecord> branchRecords =  List.of(branchRecord1, branchRecord2);

        Map<String, Object> testMap = SwiftMapper.mapHeadquarterSwiftRecordWithBranches(hqRecord,branchRecords);

        assertEquals("Address1", testMap.get("address"));
        assertEquals("Bank1", testMap.get("bankName"));
        assertEquals("PL", testMap.get("countryISO2"));
        assertEquals("POLAND", testMap.get("countryName"));
        assertEquals(true, testMap.get("isHeadquarter"));

        assertEquals("ABCDEFXXX", testMap.get("swiftCode"));
        assertTrue(testMap.containsKey("branches"));
        List<Map<String, Object>> branchList = (List<Map<String, Object>>) testMap.get("branches");
        assertEquals(2, branchList.size());
    }

    @Test
    @DisplayName("Should test single SWIFT branch Record mapping correctness")
    void testMapSingleBranchRecord(){
        SwiftRecord branchRecord = new SwiftRecord("PL", "ABCDEF111", "Bank1", "Address1", "POLAND");
        Map<String, Object> testMap = SwiftMapper.mapIndependentBranchRecord(branchRecord);

        assertEquals("Address1", testMap.get("address"));
        assertEquals("Bank1", testMap.get("bankName"));
        assertEquals("PL", testMap.get("countryISO2"));
        assertEquals("POLAND", testMap.get("countryName"));
        assertEquals("ABCDEF111", testMap.get("swiftCode"));
        assertEquals(false, testMap.get("isHeadquarter"));
        assertFalse(testMap.containsKey("branches"));
    }
}
