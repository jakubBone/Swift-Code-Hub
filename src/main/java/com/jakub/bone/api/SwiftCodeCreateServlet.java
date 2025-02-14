package com.jakub.bone.api;

import com.google.gson.Gson;
import com.jakub.bone.database.Datasource;
import com.jakub.bone.domain.SwiftRecord;
import com.jakub.bone.service.SwiftCodeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = "/v1/swift-codes")
@Log4j2
public class SwiftCodeCreateServlet extends HttpServlet {
    private Datasource datasource;
    private SwiftCodeService service;

    @Override
    public void init() throws ServletException {
        this.datasource = (Datasource) getServletContext().getAttribute("datasource");
        this.service = new SwiftCodeService(datasource.getCodeRepository());
    }

    // Endpoint 3: Add a new SWIFT code record
    // POST: /v1/swift-codes
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        try {
            SwiftRecord newRecord = gson.fromJson(request.getReader(), SwiftRecord.class);
            if (newRecord == null || newRecord.getSwiftCode() == null || newRecord.getSwiftCode().isEmpty()) {
                log.warn("POST: Invalid input - Correct data format is required");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                service.send(response, Map.of("message", "Invalid input: Correct data format is required"));
                return;
            }

            service.createSwiftRecord(newRecord);

            log.info("POST: Added new SWIFT Record with code: {}", newRecord.getSwiftCode());
            response.setStatus(HttpServletResponse.SC_OK);
            service.send(response, Map.of("message", "SWIFT Record added successfully"));
        } catch (Exception ex) {
            log.error("POST: Error while processing request", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            service.send(response, Map.of("message", "Internal server error"));
        }
    }
}
