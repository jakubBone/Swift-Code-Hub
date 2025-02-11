package com.jakub.bone.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SwiftRecord {
    private String countryIso2;
    private String swiftCode;
    private String bankName;
    private String address;
    private String town;
    private String country;
    private boolean isHeadquarter;

    public SwiftRecord(String countryIso2, String swiftCode, String bankName,
                       String address, String town, String country) {
        this.countryIso2 = countryIso2;
        this.swiftCode = swiftCode;
        this.bankName = bankName;
        this.address = address;
        this.town = town;
        this.country = country;
        this.isHeadquarter = swiftCode.endsWith("XXX");
    }
}
