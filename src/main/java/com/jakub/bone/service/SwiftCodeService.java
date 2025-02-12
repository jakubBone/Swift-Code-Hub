package com.jakub.bone.service;

import com.google.gson.Gson;
import com.jakub.bone.domain.SwiftRecord;
import com.jakub.bone.repository.SwiftCodeRepository;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class SwiftCodeService {
    private SwiftCodeRepository repository;

    public SwiftCodeService(SwiftCodeRepository repository) {
        this.repository = repository;
    }

    public SwiftRecord findSwiftRecordBySwiftCode(String swiftCode) {
        return repository.findSwiftRecordBySwiftCode(swiftCode);
    }

    public List<SwiftRecord> findBranchesRecordsByHeadquarter(String headquarterSwiftCode) {
        return repository.findAllBranchesRecordsByHeadquarter(headquarterSwiftCode);
    }

    public List<SwiftRecord> findAllSwiftRecordsByCountryIso2(String countryIso2) {
        return repository.findAllSwiftRecordsByCountryIso2(countryIso2);
    }

    public String findCountryByCountryISO2(String countryIso2) {
        return repository.findCountryByCountryISO2(countryIso2);
    }

    public void addSwiftRecord(SwiftRecord swiftRecord) {
        validateSwiftRecord(swiftRecord);

        swiftRecord.setCountryIso2(swiftRecord.getCountryIso2().toUpperCase());
        swiftRecord.setCountry(swiftRecord.getCountry().toUpperCase());

        repository.addSwiftRecord(swiftRecord);
    }

    public void deleteSwiftRecord(String swiftCode) {
        repository.deleteSwiftRecord(swiftCode);
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
        if (isNullOrEmpty(swiftRecord.getCountry())) {
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
