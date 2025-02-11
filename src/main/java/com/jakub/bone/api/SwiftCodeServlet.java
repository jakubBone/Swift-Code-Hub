package com.jakub.bone.api;

import com.google.gson.Gson;
import com.jakub.bone.database.Datasource;
import com.jakub.bone.domain.SwiftCode;
import com.jakub.bone.utills.SwiftCodeMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/v1/swift-codes/*")
public class SwiftCodeServlet extends HttpServlet {
    private Datasource database;

    @Override
    public void init() throws ServletException {
        this.database = (Datasource) getServletContext().getAttribute("database");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String path = request.getPathInfo();
            String code = path.substring(1);
            SwiftCode swiftCode = database.getCodeRepository().findSwiftCode(code);
            if (swiftCode == null) {
                send(response, Map.of("message", "record not found"));
            } else {
                if(swiftCode.isHeadquarter()){
                    List<SwiftCode> branches = database.getCodeRepository().findBranchesByHeadquarter(swiftCode.getSwiftCode());
                    send(response, SwiftCodeMapper.mapHeadquarterSwiftCode(swiftCode, branches));
                } else {
                    send(response, SwiftCodeMapper.mapSingleBranchSwiftCode(swiftCode));
                }
            }
        } catch (Exception ex){
            send(response, Map.of("error", "Internal server error"));
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
