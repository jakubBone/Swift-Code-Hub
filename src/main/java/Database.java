import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class Database {
    private final String USER = "swift_user";
    private final String PASSWORD = "swift123";
    private final String DATABASE = "swift_hub";
    private final String URL = String.format("jdbc:postgresql://localhost:%d/%s", 5432, DATABASE);
    private Connection connection;

    public Connection getDatabaseConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }
        try {
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
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
