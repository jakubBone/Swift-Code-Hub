package com.jakub.bone.api;

import com.google.gson.Gson;
import com.jakub.bone.database.Datasource;
import com.jakub.bone.domain.SwiftRecord;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = "/v1/swift-codes")
public class SwiftCodeCreateServlet extends HttpServlet {
    private Datasource datasource;

    @Override
    public void init() throws ServletException {
        this.datasource = (Datasource) getServletContext().getAttribute("database");
    }

    // Endpoint 3: Add a new SWIFT code record
    // POST: /v1/swift-codes
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();

        try {
            SwiftRecord newRecord = gson.fromJson(request.getReader(), SwiftRecord.class);

            if (newRecord == null) {
                send(response, Map.of("message", "Invalid input: Request body is empty"));
                return;
            }

            if (isNullOrEmpty(newRecord.getSwiftCode())) {
                send(response, Map.of("message", "Invalid input: SWIFT code is required"));
                return;
            }
            if (isNullOrEmpty(newRecord.getCountryIso2())) {
                send(response, Map.of("message", "Invalid input: CountryISO2 is required"));
                return;
            }
            if (isNullOrEmpty(newRecord.getCountry())) {
                send(response, Map.of("message", "Invalid input: Country is required"));
                return;
            }
            if (isNullOrEmpty(newRecord.getBankName())) {
                send(response, Map.of("message", "Invalid input: Bank name is required"));
                return;
            }
            if (isNullOrEmpty(newRecord.getAddress())) {
                send(response, Map.of("message", "Invalid input: Address is required"));
                return;
            }

            newRecord.setCountryIso2(newRecord.getCountryIso2().toUpperCase());
            newRecord.setCountry(newRecord.getCountry().toUpperCase());

            datasource.getCodeRepository().addSwiftRecord(newRecord);
            send(response, Map.of("message", "SWIFT Record added successfully"));
        } catch (Exception ex) {
            send(response, Map.of("message", "Internal server error"));
            System.err.println("Error handling POST request: " + ex.getMessage());
        }
    }

    // Helper method to send JSON responses
    public void send(HttpServletResponse response, Object message) throws IOException {
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonMessage = gson.toJson(message);
        response.getWriter().write(jsonMessage);
    }

    // Helper method to input validation
    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
