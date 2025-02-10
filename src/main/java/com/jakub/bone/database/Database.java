package com.jakub.bone.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.jakub.bone.repository.DatabaseSchema;
import com.jakub.bone.repository.SwiftCodeRepository;
import com.jakub.bone.utills.ConfigLoader;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

@Log4j2
@Getter
public class Database {
    private final String DATABASE = ConfigLoader.get("database.name");
    private final String USERNAME = ConfigLoader.get("database.username");
    private final String PASSWORD = ConfigLoader.get("database.password");
    private final String URL = String.format("jdbc:postgresql://localhost:%d/%s", 5432, DATABASE);
    private final DSLContext CONTEXT;
    private final DatabaseSchema databaseSchema;
    private final SwiftCodeRepository codeRepository;
    private Connection connection;

    public Database() throws SQLException {
        this.connection = getDatabaseConnection();
        this.CONTEXT = DSL.using(connection);
        this.databaseSchema = new DatabaseSchema(CONTEXT);
        this.codeRepository = new SwiftCodeRepository(CONTEXT);

    }

    public Connection getDatabaseConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }
        try {
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("Connection established successfully with database '{}' on port {}", DATABASE, 5432);
        } catch (SQLException ex) {
            log.error("Failed to establish connection to the database '{}'. Error: {}", DATABASE, ex.getMessage(), ex);
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                log.info("Connection to database '{}' closed successfully.", DATABASE);
            } catch (SQLException ex) {
                log.error("Failed to close connection. Error: {}", ex.getMessage(), ex);
            }
        }
    }
}
