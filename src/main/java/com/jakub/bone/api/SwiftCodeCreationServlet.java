package com.jakub.bone.api;

import com.google.gson.Gson;
import com.jakub.bone.database.Datasource;
import com.jakub.bone.domain.SwiftRecord;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class SwiftCodeCreationServlet extends HttpServlet {
    private Datasource datasource;

    @Override
    public void init() throws ServletException {
        this.datasource = (Datasource) getServletContext().getAttribute("database");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();

        try {
            SwiftRecord newRecord = gson.fromJson(request.getReader(), SwiftRecord.class);

            if(newRecord == null || newRecord.getSwiftCode() == null || newRecord.getSwiftCode().isEmpty()){
                send(response, Map.of("message", "invalid input: SWIFT code is required"));
                return;
            }

            if(newRecord.getCountryIso2() != null){
                newRecord.setCountryIso2(newRecord.getCountryIso2().toUpperCase());
            }
            if(newRecord.getCountry() != null){
                newRecord.setCountry(newRecord.getCountry().toUpperCase());
            }

            datasource.getCodeRepository().addSwiftRecord(newRecord);
            send(response, Map.of("message", "SWIFT Record added successfully"));
        } catch (Exception ex) {
            send(response, Map.of("message", "failed to add SWIFT Record"));
            System.err.println("Error handling request: " + ex.getMessage());
        }
    }

    // Helper method for sending serialized data
    public void send(HttpServletResponse response, Object message) throws IOException {
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonMessage = gson.toJson(message);
        response.getWriter().write(jsonMessage);
    }
}
