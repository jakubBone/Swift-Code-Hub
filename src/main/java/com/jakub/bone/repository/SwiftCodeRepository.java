package com.jakub.bone.repository;

import com.jakub.bone.domain.SwiftCode;
import org.jooq.DSLContext;

import java.sql.SQLException;
import java.util.List;

import static jooq.Tables.SWIFT_CODES;
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

    public SwiftCode findSwiftCode(String swift_code) {
        return context.select(
                        SWIFT_CODES.COUNTRY_ISO2,
                        SWIFT_CODES.SWIFT_CODE,
                        SWIFT_CODES.BANK_NAME,
                        SWIFT_CODES.ADDRESS,
                        SWIFT_CODES.TOWN,
                        SWIFT_CODES.COUNTRY)
                .from(SWIFT_CODES)
                .where(SWIFT_CODES.SWIFT_CODE.eq(swift_code))
                .fetchOneInto(SwiftCode.class);
    }


    public List<SwiftCode> findBranchesByHeadquarter(String headquarterSwiftCode) {
        String prefix = headquarterSwiftCode.substring(0, 8);
        return context.select(
                        SWIFT_CODES.COUNTRY_ISO2,
                        SWIFT_CODES.SWIFT_CODE,
                        SWIFT_CODES.BANK_NAME,
                        SWIFT_CODES.ADDRESS,
                        SWIFT_CODES.TOWN,
                        SWIFT_CODES.COUNTRY)
                .from(SWIFT_CODES)
                .where(SWIFT_CODES.SWIFT_CODE.like(prefix + "%"))
                .and(SWIFT_CODES.SWIFT_CODE.ne(headquarterSwiftCode))
                .fetchInto(SwiftCode.class);
    }
}
