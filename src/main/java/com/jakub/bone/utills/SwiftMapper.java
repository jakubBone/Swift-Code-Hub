package com.jakub.bone.utills;

import com.jakub.bone.domain.SwiftRecord;

import java.util.*;

public class SwiftMapper {

    public static Map<String, Object> mapSwiftCodesForCountry(String countryIso2, String countryName, List<SwiftRecord> swiftRecords) {
        Map<String, Object> headquarterMap = new LinkedHashMap<>();
        headquarterMap.put("countryISO2", countryIso2);
        headquarterMap.put("countryName", countryName);

        List<Map<String, Object>> mappedRecords = mapAllSwiftRecordsForCountry(swiftRecords);
        headquarterMap.put("swiftCodes", mappedRecords);

        return headquarterMap;
    }

    private static List<Map<String, Object>> mapAllSwiftRecordsForCountry(List<SwiftRecord> swiftRecords) {
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

    public static Map<String, Object> mapHeadquarterSwiftRecord(SwiftRecord swiftRecord, List<SwiftRecord> branches) {
        Map<String, Object> headquarterMap = new LinkedHashMap<>();
        headquarterMap.put("address", swiftRecord.getAddress());
        headquarterMap.put("bankName", swiftRecord.getBankName());
        headquarterMap.put("countryISO2", swiftRecord.getCountryIso2());
        headquarterMap.put("countryName", swiftRecord.getCountry());
        headquarterMap.put("isHeadquarter", swiftRecord.isHeadquarter());
        headquarterMap.put("swiftRecord", swiftRecord.getSwiftCode());

        List<Map<String, Object>> mappedBranches = mapAllHeadquarterBranches(branches);
        headquarterMap.put("branches", mappedBranches);

        return headquarterMap;
    }

    private static List<Map<String, Object>> mapAllHeadquarterBranches(List<SwiftRecord> branches) {
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

    public static Map<String, Object> mapSingleBranchSwiftRecord(SwiftRecord swiftRecord) {
        Map<String, Object> branchMap = new LinkedHashMap<>();
        branchMap.put("address", swiftRecord.getAddress());
        branchMap.put("bankName", swiftRecord.getBankName());
        branchMap.put("countryISO2", swiftRecord.getCountryIso2());
        branchMap.put("countryName", swiftRecord.getCountry());
        branchMap.put("isHeadquarter", swiftRecord.isHeadquarter());
        branchMap.put("swiftRecord", swiftRecord.getSwiftCode());

        return branchMap;
    }
}
