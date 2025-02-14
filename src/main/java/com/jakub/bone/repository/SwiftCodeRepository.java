package com.jakub.bone.repository;

import com.jakub.bone.domain.SwiftRecord;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;

import java.sql.SQLException;
import java.util.List;

import static jooq.Tables.SWIFT_CODES;
import static org.jooq.impl.DSL.*;

@Log4j2
@Getter
public class SwiftCodeRepository {
    private final DSLContext context;

    public SwiftCodeRepository(DSLContext context) {
        this.context = context;
    }


    /////////////////
    public void insertSwiftRecords(List<SwiftRecord> swiftCodesRecords) throws SQLException {
        try {
            for (SwiftRecord code : swiftCodesRecords) {
                context.insertInto(table("swift_codes"),
                                field("country_iso2"),
                                field("swift_code"),
                                field("bank_name"),
                                field("address"),
                                field("country"))
                        .values(code.getCountryIso2(), code.getSwiftCode(), code.getBankName(),
                                code.getAddress(), code.getCountryName())
                        .execute();
            }
        } catch (org.jooq.exception.IntegrityConstraintViolationException ex) {
            log.error("Failed to INSERT Record: {}", ex.getMessage(),ex);
        }
    }

    //////////////////////
    public SwiftRecord findBySwiftCode(String swiftCode) {
        return context.select(
                        SWIFT_CODES.COUNTRY_ISO2,
                        SWIFT_CODES.SWIFT_CODE,
                        SWIFT_CODES.BANK_NAME,
                        SWIFT_CODES.ADDRESS,
                        SWIFT_CODES.COUNTRY)
                .from(SWIFT_CODES)
                .where(SWIFT_CODES.SWIFT_CODE.eq(swiftCode))
                .fetchOneInto(SwiftRecord.class);
    }

    //
    public List<SwiftRecord> findAllByCountryIso2(String countryIso2) {
        return context.select(
                        SWIFT_CODES.COUNTRY_ISO2,
                        SWIFT_CODES.SWIFT_CODE,
                        SWIFT_CODES.BANK_NAME,
                        SWIFT_CODES.ADDRESS,
                        SWIFT_CODES.COUNTRY)
                .from(SWIFT_CODES)
                .where(SWIFT_CODES.COUNTRY_ISO2.eq(countryIso2))
                .fetchInto(SwiftRecord.class);
    }

    //
    public String findCountryByISO2(String countryIso2) {
        return context.select(SWIFT_CODES.COUNTRY)
                .from(SWIFT_CODES)
                .where(SWIFT_CODES.COUNTRY_ISO2.eq(countryIso2))
                .limit(1)
                .fetchOneInto(String.class);
    }

    public List<SwiftRecord> findAllBranchesByHeadquarter(String hqSwiftCode) {
        String prefix = hqSwiftCode.substring(0, 8);
        return context.select(
                        SWIFT_CODES.COUNTRY_ISO2,
                        SWIFT_CODES.SWIFT_CODE,
                        SWIFT_CODES.BANK_NAME,
                        SWIFT_CODES.ADDRESS,
                        SWIFT_CODES.COUNTRY)
                .from(SWIFT_CODES)
                .where(SWIFT_CODES.SWIFT_CODE.like(prefix + "%"))
                .and(SWIFT_CODES.SWIFT_CODE.ne(hqSwiftCode))
                .fetchInto(SwiftRecord.class);
    }

    //////////////////
   public void createSwiftRecord(SwiftRecord swiftRecord) {
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
                            swiftRecord.getCountryName(),
                            swiftRecord.getSwiftCode())
                    .execute();
        } catch (DataAccessException ex) {
            log.error("Failed to ADD new SWIFT Record: {}", ex.getMessage(), ex);
        }
    }

    ///
    public void deleteSwiftRecord(String swiftCode) {
        try {
            context.deleteFrom(SWIFT_CODES)
                    .where(SWIFT_CODES.SWIFT_CODE.eq(swiftCode))
                    .execute();
        } catch (DataAccessException ex) {
            log.error("Failed to DELETE SWIFT Record: {}", ex.getMessage(), ex);
        }
    }
}
