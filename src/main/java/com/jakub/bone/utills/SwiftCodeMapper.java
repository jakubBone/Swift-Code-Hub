package com.jakub.bone.utills;

import com.jakub.bone.domain.SwiftCode;

import java.util.*;

public class SwiftCodeMapper {

    public static Map<String, Object> mapHeadquarterSwiftCode(SwiftCode swiftCode, List<SwiftCode> branches) {
        Map<String, Object> headquarterMap = new LinkedHashMap<>();
        headquarterMap.put("address", swiftCode.getAddress());
        headquarterMap.put("bankName", swiftCode.getBankName());
        headquarterMap.put("countryISO2", swiftCode.getCountryIso2());
        headquarterMap.put("countryName", swiftCode.getCountry());
        headquarterMap.put("isHeadquarter", swiftCode.isHeadquarter());
        headquarterMap.put("swiftCode", swiftCode.getSwiftCode());

        List<Map<String, Object>> mappedBranches = mapAllHeadquarterBranches(branches);
        headquarterMap.put("branches", mappedBranches);

        return headquarterMap;
    }

    private static List<Map<String, Object>> mapAllHeadquarterBranches(List<SwiftCode> branches) {
        List<Map<String, Object>> list = new ArrayList<>();

        for (SwiftCode branch : branches) {
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

    public static Map<String, Object> mapSingleBranchSwiftCode(SwiftCode swiftCode) {
        Map<String, Object> branchMap = new LinkedHashMap<>();
        branchMap.put("address", swiftCode.getAddress());
        branchMap.put("bankName", swiftCode.getBankName());
        branchMap.put("countryISO2", swiftCode.getCountryIso2());
        branchMap.put("countryName", swiftCode.getCountry());
        branchMap.put("isHeadquarter", swiftCode.isHeadquarter());
        branchMap.put("swiftCode", swiftCode.getSwiftCode());

        return branchMap;
    }
}
