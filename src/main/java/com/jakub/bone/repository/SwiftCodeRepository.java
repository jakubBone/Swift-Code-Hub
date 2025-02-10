package com.jakub.bone.repository;

import com.jakub.bone.domain.SwiftCode;
import org.jooq.DSLContext;

import java.sql.SQLException;
import java.util.List;

import static org.jooq.impl.DSL.*;

public class SwiftCodeRepository {
    private final DSLContext context;

    public SwiftCodeRepository(DSLContext context) {
        this.context = context;
    }

    public void insertSwiftCodes(List<SwiftCode> swiftCodes) throws SQLException {
        for (SwiftCode code : swiftCodes) {
            context.insertInto(table("swift_codes"),
                            field("country_iso2"),
                            field("swift_code"),
                            field("bank_name"),
                            field("address"),
                            field("town"),
                            field("country"))
                    .values(code.getCountryIso2(), code.getSwiftCode(), code.getBankName(),
                            code.getAddress(), code.getTown(), code.getCountry())
                    .execute();
        }
    }
}
