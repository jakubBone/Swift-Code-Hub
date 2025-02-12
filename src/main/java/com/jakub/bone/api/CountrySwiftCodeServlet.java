package com.jakub.bone.api;

import com.jakub.bone.database.Datasource;
import com.jakub.bone.domain.SwiftRecord;
import com.jakub.bone.service.SwiftCodeService;
import com.jakub.bone.utills.SwiftMapper;
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
    private Datasource datasource;
    private SwiftCodeService service;

    @Override
    public void init() throws ServletException {
        this.datasource = (Datasource) getServletContext().getAttribute("datasource");
        this.service = new SwiftCodeService(datasource.getCodeRepository());
    }

    // Endpoint 2: Retrieve all SWIFT Records for a specific country
    // GET: /v1/swift-codes/country/{countryISO2code}
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String countryISO2 = request.getPathInfo().substring(1);
            String countryName = service.findByCountryISO2(countryISO2);
            log.info("GET: Retrieving Records for country ISO2: {}", countryISO2);

            List<SwiftRecord> swiftRecords = service.findAllByCountryIso2(countryISO2);
            if (swiftRecords == null || swiftRecords.isEmpty()) {
                log.warn("GET: No SWIFT Records found for country ISO2: {}", countryISO2);
                service.send(response, Map.of("message", "Invalid input: SWIFT Record not found"));
            } else {
                log.info("GET: Found {} SWIFT Records for country ISO2: {}", swiftRecords.size(), countryISO2);
                service.send(response, SwiftMapper.mapCountrySwiftRecords(countryISO2, countryName, swiftRecords));
            }
        } catch (Exception ex){
            log.error("GET: Error while processing request", ex);
            service.send(response, Map.of("message", "Internal server error"));
        }
    }
}
