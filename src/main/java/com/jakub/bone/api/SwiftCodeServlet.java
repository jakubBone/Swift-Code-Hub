package com.jakub.bone.api;

import com.google.gson.Gson;
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

@WebServlet(urlPatterns = {"/v1/swift-codes/*","/v1/swift-codes"})
@Log4j2
public class SwiftCodeServlet extends HttpServlet {
    private DataSource dataSource;
    private SwiftCodeService service;
    @Override
    public void init() throws ServletException {
        this.dataSource = (DataSource) getServletContext().getAttribute("datasource");
        this.service = new SwiftCodeService(dataSource.getCodeRepository());
    }

    // Endpoint 1: Retrieve details of a SWIFT Record
    // GET: /v1/swift-codes/{swift-code}
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String path = request.getPathInfo();

            if (path == null || path.isEmpty()) {
                log.warn("GET: Empty Invalid input: Empty SWIFT code request");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                service.send(response, Map.of("message", "Invalid input: Empty request. SWIFT code is required"));
                return;
            }

            String swiftCode = path.substring(1);

            SwiftRecord swiftRecord = service.findSwiftBySwiftCode(swiftCode);

            if (swiftRecord == null) {
                log.warn("GET: No SWIFT code found");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                service.send(response, Map.of("message", "Invalid input: SWIFT Record not found"));
            } else {
                if(swiftRecord.isHeadquarter()){
                    log.info("GET: Record is a headquarter");
                    response.setStatus(HttpServletResponse.SC_OK);
                    List<SwiftRecord> branches = service.findAllBranchesByHeadquarter(swiftRecord.getSwiftCode());
                    service.send(response, SwiftMapper.mapHeadquarterSwiftRecordWithBranches(swiftRecord, branches));
                } else {
                    log.info("GET: Record is a branch");
                    response.setStatus(HttpServletResponse.SC_OK);
                    service.send(response, SwiftMapper.mapIndependentBranchRecord(swiftRecord));
                }
            }
        } catch (Exception ex){
            log.error("GET: Error while processing the request", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            service.send(response, Map.of("message", "Internal server error"));
        }
    }

    // Endpoint 3: Add a new SWIFT code Record
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

            boolean recordExists = dataSource.getCodeRepository().existsBySwiftCode(newRecord.getSwiftCode());

            if(recordExists){
                log.warn("POST: Duplicate - SWIFT code exists in data base");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                service.send(response, Map.of("message", "Duplicate: SWIFT code already exists"));
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

    // Endpoint 4: Delete a SWIFT code Record
    // DELETE: /v1/swift-codes/{swift-code}
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String path = request.getPathInfo();

            if (path == null || path.isEmpty()) {
                log.warn("DELETE: Empty Invalid input: Empty SWIFT code request");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                service.send(response, Map.of("message", "Invalid input: Empty request. SWIFT code is required"));
                return;
            }

            String swiftCode = path.substring(1);
            SwiftRecord swiftRecord = service.findSwiftBySwiftCode(swiftCode);
            if (swiftRecord == null) {
                log.warn("DELETE: SWIFT Record not found for code: {}", swiftCode);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                service.send(response, Map.of("message", "Invalid input: SWIFT Record not found"));
            } else {
                service.deleteSwiftRecord(swiftCode);
                log.info("DELETE: Successfully deleted SWIFT record for code: {}", swiftCode);
                response.setStatus(HttpServletResponse.SC_OK);
                service.send(response, Map.of("message", "SWIFT Record deleted successfully"));
            }
        } catch (Exception ex){
            log.error("DELETE: Error while processing the request", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            service.send(response, Map.of("message", "Internal server error"));
        }
    }
}
