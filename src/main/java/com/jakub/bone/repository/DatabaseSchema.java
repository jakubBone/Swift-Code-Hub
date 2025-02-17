package com.jakub.bone.repository;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

public class DatabaseSchema {
    private final DSLContext context;

    public DatabaseSchema(DSLContext context) {
        this.context = context;
        createTable();
    }

    public void createTable() {
        context.createTableIfNotExists("swift_code")
                .column("id", SQLDataType.INTEGER.identity(true))
                .column("country_iso2", SQLDataType.VARCHAR)
                .column("swift_code", SQLDataType.VARCHAR)
                .column("bank_name", SQLDataType.VARCHAR)
                .column("address", SQLDataType.VARCHAR)
                .column("country", SQLDataType.VARCHAR)
                .constraints(
                        DSL.constraint("PK_SWING_CODE").primaryKey("id"))
                .execute();
    }

    public void truncateTable(){
        context.truncate("swift_code").restartIdentity().execute();
    }
}
