package com.jakub.bone.repository;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

public class DatabaseSchema {
    private final DSLContext CONTEXT;

    public DatabaseSchema(DSLContext dsl) {
        this.CONTEXT = dsl;
        createTable();
    }

    public void createTable() {
        CONTEXT.createTableIfNotExists("swing_codes")
                .column("id", SQLDataType.INTEGER.identity(true))
                .column("country_iso2", SQLDataType.VARCHAR)
                .column("swift_code", SQLDataType.VARCHAR)
                .column("bank_name", SQLDataType.VARCHAR)
                .column("address", SQLDataType.VARCHAR)
                .column("town", SQLDataType.VARCHAR)
                .column("country", SQLDataType.VARCHAR)
                .constraints(
                        DSL.constraint("PK_PLANES").primaryKey("id"))
                .execute();
    }

    public void clearTables(){
        CONTEXT.truncate("swing_codes").restartIdentity().execute();
    }
}
