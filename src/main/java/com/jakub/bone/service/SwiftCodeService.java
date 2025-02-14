package com.jakub.bone.service;

import com.google.gson.Gson;
import com.jakub.bone.domain.SwiftRecord;
import com.jakub.bone.repository.SwiftCodeRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.jooq.DSLContext;

import java.io.IOException;
import java.util.List;

public class SwiftCodeService {
    private SwiftCodeRepository repository;

    public SwiftCodeService(SwiftCodeRepository repository) {
        this.repository = repository;
    }

    public SwiftRecord findSwiftBySwiftCode(String swiftCode) {
        return repository.findBySwiftCode(swiftCode);
    }

    public List<SwiftRecord> findAllBranchesByHeadquarter(String hqSwiftCode) {
        return repository.findAllBranchesByHeadquarter(hqSwiftCode);
    }

    public List<SwiftRecord> findAllByCountryIso2(String countryIso2) {
        return repository.findAllByCountryIso2(countryIso2);
    }

    public String findByCountryISO2(String countryIso2) {
        return repository.findCountryByISO2(countryIso2);
    }

    public void createSwiftRecord(SwiftRecord swiftRecord) {
        validateSwiftRecord(swiftRecord);

        swiftRecord.setCountryIso2(swiftRecord.getCountryIso2().toUpperCase());
        swiftRecord.setCountryName(swiftRecord.getCountryName().toUpperCase());

        DSLContext context = repository.getContext();
        context.transaction(configuration -> {
            repository.createSwiftRecord(swiftRecord);
        });
    }

    public void deleteSwiftRecord(String swiftCode) {
        DSLContext context = repository.getContext();
        context.transaction(configuration -> {
            repository.deleteSwiftRecord(swiftCode);
        });
    }

    private void validateSwiftRecord(SwiftRecord swiftRecord) {
        if (swiftRecord == null) {
            throw new IllegalArgumentException("SwiftRecord cannot be null");
        }
        if (isNullOrEmpty(swiftRecord.getSwiftCode())) {
            throw new IllegalArgumentException("SWIFT code is required");
        }
        if (isNullOrEmpty(swiftRecord.getCountryIso2())) {
            throw new IllegalArgumentException("Country ISO2 is required");
        }
        if (isNullOrEmpty(swiftRecord.getCountryName())) {
            throw new IllegalArgumentException("Country is required");
        }
        if (isNullOrEmpty(swiftRecord.getBankName())) {
            throw new IllegalArgumentException("Bank name is required");
        }
        if (isNullOrEmpty(swiftRecord.getAddress())) {
            throw new IllegalArgumentException("Address is required");
        }
    }

    // Helper method to input validation
    public boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
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
