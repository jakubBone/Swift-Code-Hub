package com.jakub.bone.api;

import com.jakub.bone.database.DataSource;
import com.jakub.bone.domain.SwiftRecord;
import com.jakub.bone.service.SwiftCodeService;
import com.jakub.bone.utils.SwiftMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/v1/swift-codes/country/*")
@Log4j2
public class CountrySwiftCodeServlet extends HttpServlet {
    private DataSource dataSource;
    private SwiftCodeService service;

    @Override
    public void init() throws ServletException {
        this.dataSource = (DataSource) getServletContext().getAttribute("datasource");
        this.service = new SwiftCodeService(dataSource.getCodeRepository());
    }

    // Endpoint 2: Retrieve all SWIFT Records for a specific country
    // GET: /v1/swift-codes/country/{countryISO2code}
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String path = request.getPathInfo();

            if (path == null) {
                log.warn("GET: Empty Invalid input: Empty CountryISO2 request");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                service.send(response, Map.of("message", "Invalid input: Empty request. CountryISO2 code is required"));
                return;
            }

            String countryISO2 = request.getPathInfo().substring(1);
            String countryName = service.findByCountryISO2(countryISO2);
            log.info("GET: Retrieving Records for country ISO2: {}", countryISO2);

            List<SwiftRecord> swiftRecords = service.findAllByCountryIso2(countryISO2);
            if (swiftRecords == null || swiftRecords.isEmpty()) {
                log.warn("GET: No SWIFT Records found for country ISO2: {}", countryISO2);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                service.send(response, Map.of("message", "Invalid input: SWIFT Record not found"));
            } else {
                log.info("GET: Found {} SWIFT Records for country ISO2: {}", swiftRecords.size(), countryISO2);
                response.setStatus(HttpServletResponse.SC_OK);
                service.send(response, SwiftMapper.mapCountrySwiftRecords(countryISO2, countryName, swiftRecords));
            }
        } catch (Exception ex){
            log.error("GET: Error while processing request", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            service.send(response, Map.of("message", "Internal server error"));
        }
    }
}
