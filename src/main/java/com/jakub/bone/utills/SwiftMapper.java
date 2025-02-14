package com.jakub.bone.utills;

import com.jakub.bone.domain.SwiftRecord;

import java.util.*;

public class SwiftMapper {

    public static Map<String, Object> mapCountrySwiftRecords(String countryIso2, String countryName, List<SwiftRecord> swiftRecords) {
        Map<String, Object> headquarterMap = new LinkedHashMap<>();
        headquarterMap.put("countryISO2", countryIso2);
        headquarterMap.put("countryName", countryName);

        List<Map<String, Object>> mappedRecords = mapSwiftRecords(swiftRecords);
        headquarterMap.put("swiftCodes", mappedRecords);

        return headquarterMap;
    }

    private static List<Map<String, Object>> mapSwiftRecords(List<SwiftRecord> swiftRecords) {
        List<Map<String, Object>> list = new ArrayList<>();

        for (SwiftRecord branch : swiftRecords) {
            Map<String, Object> branchMap = new LinkedHashMap<>();
            branchMap.put("address", branch.getAddress());
            branchMap.put("bankName", branch.getBankName());
            branchMap.put("countryISO2", branch.getCountryIso2());
            branchMap.put("isHeadquarter", branch.isHeadquarter());
            branchMap.put("swiftCode", branch.getSwiftCode());
            list.add(branchMap);
        }
        return list;
    }

    public static Map<String, Object> mapHeadquarterSwiftRecordWithBranches(SwiftRecord hqSwiftRecord, List<SwiftRecord> branches) {
        Map<String, Object> headquarterMap = new LinkedHashMap<>();
        headquarterMap.put("address", hqSwiftRecord.getAddress());
        headquarterMap.put("bankName", hqSwiftRecord.getBankName());
        headquarterMap.put("countryISO2", hqSwiftRecord.getCountryIso2());
        headquarterMap.put("countryName", hqSwiftRecord.getCountryName());
        headquarterMap.put("isHeadquarter", hqSwiftRecord.isHeadquarter());
        headquarterMap.put("swiftCode", hqSwiftRecord.getSwiftCode());

        List<Map<String, Object>> mappedBranches = mapHqBranchRecords(branches);
        headquarterMap.put("branches", mappedBranches);

        return headquarterMap;
    }

    private static List<Map<String, Object>> mapHqBranchRecords(List<SwiftRecord> branches) {
        List<Map<String, Object>> branchList = new ArrayList<>();

        for (SwiftRecord branch: branches) {
            Map<String, Object> branchMap = new LinkedHashMap<>();
            branchMap.put("address", branch.getAddress());
            branchMap.put("bankName", branch.getBankName());
            branchMap.put("countryISO2", branch.getCountryIso2());
            branchMap.put("isHeadquarter", branch.isHeadquarter());
            branchMap.put("swiftCode", branch.getSwiftCode());
            branchList.add(branchMap);
        }
        return branchList;
    }

    public static Map<String, Object> mapIndependentBranchRecord(SwiftRecord branchRecord) {
        Map<String, Object> branchMap = new LinkedHashMap<>();
        branchMap.put("address", branchRecord.getAddress());
        branchMap.put("bankName", branchRecord.getBankName());
        branchMap.put("countryIso2", branchRecord.getCountryIso2());
        branchMap.put("countryName", branchRecord.getCountryName());
        branchMap.put("isHeadquarter", branchRecord.isHeadquarter());
        branchMap.put("swiftCode", branchRecord.getSwiftCode());

        return branchMap;
    }
}
