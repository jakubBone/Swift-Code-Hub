package com.jakub.bone.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.jakub.bone.repository.DatabaseSchema;
import com.jakub.bone.repository.SwiftCodeRepository;
import com.jakub.bone.utils.ConfigLoader;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

@Log4j2
@Getter
public class DataSource {
    private final String url = ConfigLoader.get("database.url");
    private final String database = ConfigLoader.get("database.name");
    private final String username = ConfigLoader.get("database.username");
    private final String password = ConfigLoader.get("database.password");
    private final DSLContext context;
    private final DatabaseSchema databaseSchema;
    private final SwiftCodeRepository codeRepository;
    private Connection connection;

    public DataSource() throws SQLException {
        this.connection = getDatabaseConnection();
        this.context = DSL.using(connection);
        this.databaseSchema = new DatabaseSchema(context);
        this.codeRepository = new SwiftCodeRepository(context);
    }

    public Connection getDatabaseConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }

        // 5 seconds delay before trying to connect
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted while waiting", ex);
            throw new SQLException("Thread was interrupted while waiting", ex);
        }

        try {
            this.connection = DriverManager.getConnection(url, username, password);
            log.info("Connection established successfully with database '{}' on port {}", database, 5432);
        } catch (SQLException ex) {
            log.error("Failed to establish connection to the database '{}'. Error: {}", database, ex.getMessage(), ex);
            throw ex;
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                log.info("Connection to database '{}' closed successfully.", database);
            } catch (SQLException ex) {
                log.error("Failed to close connection. Error: {}", ex.getMessage(), ex);
            }
        }
    }
}
