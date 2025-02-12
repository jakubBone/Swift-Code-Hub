package com.jakub.bone.api;

import com.google.gson.Gson;
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

@WebServlet(urlPatterns = "/v1/swift-codes/*")
@Log4j2
public class SwiftCodeServlet extends HttpServlet {
    private Datasource datasource;
    private SwiftCodeService service
    @Override
    public void init() throws ServletException {
        this.datasource = (Datasource) getServletContext().getAttribute("datasource");
        this.service = new SwiftCodeService(datasource.getCodeRepository());
    }

    // Endpoint 1: Retrieve details of a SWIFT Record
    // GET: /v1/swift-codes/{swift-code}
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String path = request.getPathInfo();
            String swiftCode = path.substring(1);

            SwiftRecord swiftRecord = service.findSwiftRecordBySwiftCode(swiftCode)

            if (swiftRecord == null) {
                log.warn("GET: No SWIFT code provided in the path");
                service.send(response, Map.of("message", "Invalid input: SWIFT Record not found"));
            } else {
                if(swiftRecord.isHeadquarter()){
                    log.info("GET: Record is a headquarter");
                    List<SwiftRecord> branches = service.findBranchesRecordsByHeadquarter(swiftRecord.getSwiftCode())
                    service.send(response, SwiftMapper.mapHeadquarterSwiftRecord(swiftRecord, branches));
                } else {
                    log.info("GET: Record is a branch");
                    service.send(response, SwiftMapper.mapSingleBranchSwiftRecord(swiftRecord));
                }
            }
        } catch (Exception ex){
            log.error("GET: Error while processing the request", ex);
            service.send(response, Map.of("message", "Internal server error"));
        }
    }

    // Endpoint 4: Delete a SWIFT code record
    // DELETE: /v1/swift-codes/{swift-code}
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String path = request.getPathInfo();
            if (path == null ) {
                log.warn("DELETE: No SWIFT code provided in the path");
                service.send(response, Map.of("message", "Invalid input: SWIFT code is required"));
                return;
            }

            String swiftCode = path.substring(1);
            SwiftRecord swiftRecord = service.findSwiftRecordBySwiftCode(swiftCode);
            if (swiftRecord == null) {
                log.warn("DELETE: SWIFT Record not found for code: {}", swiftCode);
                service.send(response, Map.of("message", "Invalid input: SWIFT code not found"));
            } else {
                service.deleteSwiftRecord(swiftCode);
                log.info("DELETE: Successfully deleted SWIFT record for code: {}", swiftCode);
                service.send(response, Map.of("message", "SWIFT Record deleted successfully"));
            }
        } catch (Exception ex){
            log.error("DELETE: Error while processing the request", ex);
            service.send(response, Map.of("message", "Internal server error"));
            System.err.println("Error handling DELETE request: " + ex.getMessage());
        }
    }
}
