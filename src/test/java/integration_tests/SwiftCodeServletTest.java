package integration_tests;

import com.jakub.bone.api.CountrySwiftCodeServlet;
import com.jakub.bone.api.SwiftCodeCreateServlet;
import com.jakub.bone.api.SwiftCodeServlet;
import com.jakub.bone.database.Datasource;
import com.jakub.bone.domain.SwiftRecord;
import com.jakub.bone.utills.SwiftMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

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
    @DisplayName("Should test GET SWIFT Headquarter Record by SWIFT code with correct input")
    void testGetHeadquarterBySwiftCodeCorrectInput() {
        waitForUpdate();

        SwiftRecord hqRecord = new SwiftRecord("PL", "ABCDEFGHXXX", "Bank1", "Address1", "POLAND");
        SwiftRecord branchRecord = new SwiftRecord("PL", "ABCDEFGH123", "Bank1", "Address1", "POLAND");
        datasource.getCodeRepository().createSwiftRecord(hqRecord);
        datasource.getCodeRepository().createSwiftRecord(branchRecord);

        // GET: /v1/swift-codes/{swift-code}
        RestAssured.baseURI = "http://localhost:8080";
        Response response = RestAssured.get("/v1/swift-codes/ABCDEFGHXXX");

        response.then().assertThat()
                .statusCode(200)
                .body("countryISO2", equalTo("PL"))
                .body("swiftCode", equalTo("ABCDEFGHXXX"))
                .body("bankName", equalTo("Bank1"))
                .body("address", equalTo("Address1"))
                .body("countryName", equalTo("POLAND"))
                .body("isHeadquarter", equalTo(true))
                .body("swiftCode", not(equalTo("INCORRECT_CODE")))
                .body("branches[0].address", equalTo("Address1"))
                .body("branches[0].bankName", equalTo("Bank1"))
                .body("branches[0].countryISO2", equalTo("PL"))
                .body("branches[0].isHeadquarter", equalTo(false))
                .body("branches[0].swiftCode", equalTo("ABCDEFGH123"));
    }

    @Test
    @DisplayName("Should test GET SWIFT Headquarter Record by SWIFT code with incorrect input")
    void testGetHeadquarterBySwiftCodeIncorrectInput() {
        waitForUpdate();
        String incorrectSwiftCode =  "UNAVAILABLE";

        // GET: /v1/swift-codes/{swift-code}
        RestAssured.baseURI = "http://localhost:8080";
        Response response = RestAssured.get("/v1/swift-codes/" + incorrectSwiftCode);

        response.then().assertThat()
                .statusCode(404)
                .body("message",  equalTo("Invalid input: SWIFT Record not found"));
    }

    @Test
    @DisplayName("Should test GET SWIFT Branch Record by SWIFT code with correct input")
    void testGetBranchBySwiftCodeCorrectInput() {
        waitForUpdate();
        SwiftRecord branchRecord = new SwiftRecord("PL", "ABCDEFGH123", "Bank1", "Address1", "POLAND");
        datasource.getCodeRepository().createSwiftRecord(branchRecord);

        // GET: /v1/swift-codes/{swift-code}
        RestAssured.baseURI = "http://localhost:8080";
        Response response = RestAssured.get("/v1/swift-codes/ABCDEFGH123");

        response.then().assertThat()
                .statusCode(200)
                .body("address", equalTo("Address1"))
                .body("bankName", equalTo("Bank1"))
                .body("countryISO2", equalTo("PL"))
                .body("countryName", equalTo("POLAND"))
                .body("isHeadquarter", equalTo(false))
                .body("swiftCode", equalTo("ABCDEFGH123"));
    }

    @Test
    @DisplayName("Should test GET SWIFT Branch Record by SWIFT code with incorrect input")
    void testGetBranchBySwiftCodeIncorrectInput() {
        waitForUpdate();
        String incorrectSwiftCode =  "UNAVAILABLE";

        // GET: /v1/swift-codes/{swift-code}
        RestAssured.baseURI = "http://localhost:8080";
        Response response = RestAssured.get("/v1/swift-codes/" + incorrectSwiftCode);

        response.then().assertThat()
                .statusCode(404)
                .body("message",  equalTo("Invalid input: SWIFT Record not found"));
    }

    @Test
    @DisplayName("Should test GET SWIFT Records by countryISO2 with correct input")
    void testGetRecordsByCountryISO2CorrectInput() {
        waitForUpdate();
        SwiftRecord record1 = new SwiftRecord("PL", "ABCDEFXXX", "Bank1", "Address1", "POLAND");
        SwiftRecord record2 = new SwiftRecord("PL", "ABCDEF111", "Bank2", "Address2", "POLAND");

        datasource.getCodeRepository().createSwiftRecord(record1);
        datasource.getCodeRepository().createSwiftRecord(record2);

        // GET: /v1/swift-codes/country/{countryISO2code}
        RestAssured.baseURI = "http://localhost:8080";
        Response response = RestAssured.get("/v1/swift-codes/country/PL");

        response.then().assertThat()
                .statusCode(200)
                .body("countryISO2", equalTo("PL"))
                .body("countryName", equalTo("POLAND"))
                .body("swiftCodes", hasSize(2))
                .body("swiftCodes[0].swiftCode", equalTo("ABCDEFXXX"))
                .body("swiftCodes[0].bankName", equalTo("Bank1"))
                .body("swiftCodes[0].address", equalTo("Address1"))
                .body("swiftCodes[0].isHeadquarter", equalTo(true))
                .body("swiftCodes[1].swiftCode", equalTo("ABCDEF111"))
                .body("swiftCodes[1].bankName", equalTo("Bank2"))
                .body("swiftCodes[1].address", equalTo("Address2"))
                .body("swiftCodes[1].isHeadquarter", equalTo(false));
    }

    @Test
    @DisplayName("Should test GET SWIFT Records by countryISO2 with incorrect input")
    void testGetRecordsByCountryISO2IncorrectInput() {
        waitForUpdate();
        String incorrectCountryISO2 = "UNAVAILABLE";

        // GET: /v1/swift-codes/country/{countryISO2code}
        RestAssured.baseURI = "http://localhost:8080";
        Response response = RestAssured.get("/v1/swift-codes/country/" + incorrectCountryISO2);

        response.then().assertThat()
                .statusCode(404)
                .body("message",  equalTo("Invalid input: SWIFT Record not found"));
    }


    @Test
    @DisplayName("Should test SWIFT Record DELETE by SWIFT code with correct input")
    void testDeleteRecordBySwiftCodeCorrectInput() {
        waitForUpdate();

        SwiftRecord record = new SwiftRecord("PL", "ABCDEFXXX", "Bank1", "Address1", "POLAND");
        datasource.getCodeRepository().createSwiftRecord(record);

        // DELETE: /v1/swift-codes/{swift-code}
        RestAssured.baseURI = "http://localhost:8080";
        Response response = RestAssured.delete("/v1/swift-codes/ABCDEFXXX");

        SwiftRecord result = datasource.getCodeRepository().findBySwiftCode("ABCDEFXXX");

        response.then().assertThat()
                .statusCode(200)
                .body("message", equalTo("SWIFT Record deleted successfully"));
        assertNull(result);
    }

    @Test
    @DisplayName("Should test SWIFT Record DELETE by SWIFT code with incorrect input")
    void testDeleteRecordBySwiftCodeIncorrectInput() {
        waitForUpdate();
        String incorrectSwiftCode =  "UNAVAILABLE";

        // DELETE: /v1/swift-codes/{swift-code}
        RestAssured.baseURI = "http://localhost:8080";
        Response response = RestAssured.delete("/v1/swift-codes/" + incorrectSwiftCode);

        SwiftRecord result = datasource.getCodeRepository().findBySwiftCode("ABCDEFGHXXX");

        response.then().assertThat()
                .statusCode(404)
                .body("message",  equalTo("Invalid input: SWIFT Record not found"));
    }

    @Test
    @DisplayName("Should test SWIFT Record POST with correct input")
    void testPostRecordCorrectInput() {
        waitForUpdate();

        SwiftRecord hqRecord = new SwiftRecord("PL", "ABCDEFGHXXX", "Bank1", "Address1", "POLAND");
        Map<String, Object> requestBody = SwiftMapper.mapIndependentBranchRecord(hqRecord);

        // POST: /v1/swift-codes
        RestAssured.baseURI = "http://localhost:8080";
        Response response = RestAssured.given()
                    .contentType("application/json")
                    .body(requestBody)
                    .post("/v1/swift-codes");

        response.then().assertThat()
                .statusCode(200)
                .body("message",  equalTo("SWIFT Record added successfully"));


        SwiftRecord createdRecord = datasource.getCodeRepository().findBySwiftCode("ABCDEFGHXXX");

        assertEquals("ABCDEFGHXXX", createdRecord.getSwiftCode());
    }

    @Test
    @DisplayName("Should test SWIFT Record POST with correct input")
    void testPostRecordIncorrectInput() {
        waitForUpdate();

        Map<String, Object> invalidBody = Map.of(
                "bankName", "Bank1",
                "address", "Address1",
                // Invalid data input
                "isHeadquarter", true
        );

        // POST: /v1/swift-codes
        RestAssured.baseURI = "http://localhost:8080";
        Response response = RestAssured.given()
                    .contentType("application/json")
                    .body(invalidBody)
                    .post("/v1/swift-codes");

        response.then().assertThat()
                .statusCode(404)
                .body("message",  equalTo("Invalid input: Correct data format is required"));

    }
}
