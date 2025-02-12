package com.jakub.bone.repository;

import com.jakub.bone.domain.SwiftRecord;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;

import java.sql.SQLException;
import java.util.List;

import static jooq.Tables.SWIFT_CODES;
import static org.jooq.impl.DSL.*;

public class SwiftCodeRepository {
    private final DSLContext context;

    public SwiftCodeRepository(DSLContext context) {
        this.context = context;
    }

    public void insertSwiftRecords(List<SwiftRecord> swiftCodesRecords) throws SQLException {
        for (SwiftRecord code : swiftCodesRecords) {
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

    public SwiftRecord findSwiftRecordBySwiftCode(String swift_code) {
        return context.select(
                        SWIFT_CODES.COUNTRY_ISO2,
                        SWIFT_CODES.SWIFT_CODE,
                        SWIFT_CODES.BANK_NAME,
                        SWIFT_CODES.ADDRESS,
                        SWIFT_CODES.TOWN,
                        SWIFT_CODES.COUNTRY)
                .from(SWIFT_CODES)
                .where(SWIFT_CODES.SWIFT_CODE.eq(swift_code))
                .fetchOneInto(SwiftRecord.class);
    }

    public List<SwiftRecord> findAllSwiftRecordsByCountryIso2(String countryIso2) {
        return context.select(
                        SWIFT_CODES.COUNTRY_ISO2,
                        SWIFT_CODES.SWIFT_CODE,
                        SWIFT_CODES.BANK_NAME,
                        SWIFT_CODES.ADDRESS,
                        SWIFT_CODES.TOWN,
                        SWIFT_CODES.COUNTRY)
                .from(SWIFT_CODES)
                .where(SWIFT_CODES.COUNTRY_ISO2.eq(countryIso2))
                .fetchInto(SwiftRecord.class);
    }

    public String findCountryByCountryISO2(String countryIso2) {
        return context.select(SWIFT_CODES.COUNTRY)
                .from(SWIFT_CODES)
                .where(SWIFT_CODES.COUNTRY_ISO2.eq(countryIso2))
                .limit(1)
                .fetchOneInto(String.class);
    }

    public List<SwiftRecord> findAllBranchesRecordsByHeadquarter(String headquarterSwiftCode) {
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
                .fetchInto(SwiftRecord.class);
    }

    public void addSwiftRecord(SwiftRecord swiftRecord) {
        try {
            context.insertInto(SWIFT_CODES,
                            SWIFT_CODES.ADDRESS,
                            SWIFT_CODES.BANK_NAME,
                            SWIFT_CODES.COUNTRY_ISO2,
                            SWIFT_CODES.COUNTRY,
                            SWIFT_CODES.SWIFT_CODE)
                    .values(
                            swiftRecord.getAddress(),
                            swiftRecord.getBankName(),
                            swiftRecord.getCountryIso2(),
                            swiftRecord.getCountry(),
                            swiftRecord.getSwiftCode())
                    .execute();
        } catch (DataAccessException ex) {
            System.err.println("Failed to add new SWIFT Record: " + ex.getMessage());
        }
    }

    public void deleteSwiftRecord(String swiftCode) {
        try {
            context.deleteFrom(SWIFT_CODES)
                    .where(SWIFT_CODES.SWIFT_CODE.eq(swiftCode))
                    .execute();
        } catch (DataAccessException ex) {
            System.err.println("Failed to delete SWIFT Record: " + ex.getMessage());
        }
    }
}
