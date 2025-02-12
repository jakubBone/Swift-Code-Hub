package com.jakub.bone.api;

import com.google.gson.Gson;
import com.jakub.bone.database.Datasource;
import com.jakub.bone.domain.SwiftRecord;
import com.jakub.bone.utills.SwiftMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/v1/swift-codes/country/*")
public class CountrySwiftCodeServlet extends HttpServlet {
    private Datasource datasource;

    @Override
    public void init() throws ServletException {
        this.datasource = (Datasource) getServletContext().getAttribute("datasource");
    }

    // Endpoint 2: Retrieve all SWIFT Records for a specific country
    // GET: /v1/swift-codes/country/{countryISO2code}
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String countryISO2 = request.getPathInfo().substring(1);
            String countryName = datasource.getCodeRepository().findCountryByCountryISO2(countryISO2);
            List<SwiftRecord> swiftRecords = datasource.getCodeRepository().findAllSwiftRecordsByCountryIso2(countryISO2);
            if (swiftRecords == null) {
                send(response, Map.of("message", "Invalid input: SWIFT Record not found"));
            } else {
                send(response, SwiftMapper.mapSwiftCodesForCountry(countryISO2, countryName, swiftRecords));
            }
        } catch (Exception ex){
            send(response, Map.of("message", "Internal server error"));
            System.err.println("Error handling GET request: " + ex.getMessage());
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
}
