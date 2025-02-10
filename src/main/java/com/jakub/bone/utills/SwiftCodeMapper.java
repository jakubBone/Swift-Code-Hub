package com.jakub.bone.utills;

import com.jakub.bone.domain.SwiftCode;

import java.util.LinkedHashMap;
import java.util.Map;

public class SwiftCodeMapper {

    /*public static Map<String, Object> mapHeadquarterSwiftCode(SwiftCode swiftCode){
        Map<String, Object> planeMap = new LinkedHashMap<>();
        planeMap.put("address", swiftCode.getAddress());
        planeMap.put("bankName", swiftCode.getBankName());
        planeMap.put("countryISO2", swiftCode.getCountryIso2());
        planeMap.put("countryName", swiftCode.getCountry();
        planeMap.put("isHeadquarter", swiftCode.isHeadquarter());
        planeMap.put("swiftCode", swiftCode.getSwiftCode());

        // branch jest wtedy jak ostatnie liczby swift_code są 901 zamiast XXX

        Map<String, Object> branchesMap = new LinkedHashMap<>();
        locationMap.put("x", plane.getNavigator().getLocation().getX());
        locationMap.put("y", plane.getNavigator().getLocation().getY());
        locationMap.put("altitude", plane.getNavigator().getLocation().getAltitude());

        return planeMap;
    }*/

    public static Map<String, Object> mapBranchSwiftCode(SwiftCode swiftCode){
        Map<String, Object> planeMap = new LinkedHashMap<>();
        planeMap.put("address", swiftCode.getAddress());
        planeMap.put("bankName", swiftCode.getBankName());
        planeMap.put("countryISO2", swiftCode.getCountryIso2());
        planeMap.put("countryName", swiftCode.getCountry());
        planeMap.put("isHeadquarter", swiftCode.isHeadquarter());
        planeMap.put("swiftCode", swiftCode.getSwiftCode());

        return planeMap;
    }
}
