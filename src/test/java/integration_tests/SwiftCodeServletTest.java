package integration_tests;

import com.jakub.bone.api.CountrySwiftCodeServlet;
import com.jakub.bone.api.SwiftCodeCreateServlet;
import com.jakub.bone.api.SwiftCodeServlet;
import com.jakub.bone.database.Datasource;
import com.jakub.bone.domain.SwiftRecord;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNull;

class SwiftCodeServletTest {
    Server server;
    Datasource datasource;
    @BeforeEach
    void setUp() throws SQLException {
       new Thread(() -> {
            server = new Server(8080);

            ServletContextHandler context = new ServletContextHandler();
            server.setHandler(context);

            try {
                datasource = new Datasource();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            datasource.getDatabaseSchema().clearTables();

            context.setAttribute("datasource", datasource);

            // Init Servlet
            context.addServlet(new ServletHolder(new SwiftCodeServlet()), "/v1/swift-codes/*");
            context.addServlet(new ServletHolder(new CountrySwiftCodeServlet()), "/v1/swift-codes/country/*");
            context.addServlet(new ServletHolder(new SwiftCodeCreateServlet()), "/v1/swift-codes");

            try {
                server.start();
                try {
                    server.join();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }).start();
    }

    @AfterEach
    void tearDown() throws Exception {
        datasource.getDatabaseSchema().clearTables();
        datasource.closeConnection();
        server.stop();
    }

    // Helper method to wait for the server to start
    private void waitForUpdate() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @DisplayName("Should test SWIFT Record GET by SWIFT code")
    void testGetRecordBySwiftCode() {
        waitForUpdate();

        SwiftRecord record = new SwiftRecord("PL", "ABCDEFXXX", "Bank1", "Address1", "POLAND");
        datasource.getCodeRepository().createSwiftRecord(record);

        // GET: /v1/swift-codes/{swift-code}
        RestAssured.baseURI = "http://localhost:8080";
        Response response = RestAssured.get("/v1/swift-codes/ABCDEFXXX");

        response.then().assertThat().statusCode(200);
        response.then().assertThat().body("countryISO2", equalTo("PL"));
        response.then().assertThat().body("swiftCode", equalTo("ABCDEFXXX"));
        response.then().assertThat().body("bankName", equalTo("Bank1"));
        response.then().assertThat().body("address", equalTo("Address1"));
        response.then().assertThat().body("countryName", equalTo("POLAND"));
        response.then().assertThat().body("swiftCode", not(equalTo("INCORRECT_CODE")));
    }

    @Test
    @DisplayName("Should test SWIFT Record DELETE by SWIFT code")
    void testDeleteRecordBySwiftCode() {
        waitForUpdate();

        SwiftRecord record = new SwiftRecord("PL", "ABCDEFXXX", "Bank1", "Address1", "POLAND");
        datasource.getCodeRepository().createSwiftRecord(record);

        // DELETE: /v1/swift-codes/{swift-code}
        RestAssured.baseURI = "http://localhost:8080";
        Response response = RestAssured.delete("/v1/swift-codes/ABCDEFXXX");

        SwiftRecord result = datasource.getCodeRepository().findBySwiftCode("ABCDEFXXX");

        response.then().assertThat().statusCode(200);
        response.then().assertThat().body("message", equalTo("SWIFT Record deleted successfully"));
        assertNull(result);
    }
}
