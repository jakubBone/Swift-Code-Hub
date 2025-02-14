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
    private String countryName;
    private boolean isHeadquarter;

    public SwiftRecord(String countryIso2, String swiftCode, String bankName,
                       String address, String countryName) {
        this.countryIso2 = countryIso2;
        this.swiftCode = swiftCode;
        this.bankName = bankName;
        this.address = address;
        this.countryName = countryName;
        this.isHeadquarter = swiftCode.endsWith("XXX");
    }

}
