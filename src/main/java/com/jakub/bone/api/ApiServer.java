package com.jakub.bone.api;

import com.jakub.bone.database.Datasource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.sql.SQLException;

public class ApiServer {
    public static void main(String[] args) throws SQLException {
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler();
        server.setHandler(context);

        Datasource datasource = new Datasource();
        context.setAttribute("datasource", datasource);

        // Init Servlet
        context.addServlet(new ServletHolder(new SwiftCodeServlet()), "/v1/swift-codes/*");
        context.addServlet(new ServletHolder(new CountrySwiftCodeServlet()), "/v1/swift-codes/country/*");
        context.addServlet(new ServletHolder(new SwiftCodeCreateServlet()), "/v1/swift-codes");

        try {
            server.start();
            System.out.println("Server is running on http://localhost:8080");
            try {
                server.join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                System.err.println("API Server interrupted: " + ex.getMessage());
            }
        } catch (Exception ex) {
            System.err.println("Failed to start API Server: " + ex.getMessage());
        } finally {
            try {
                server.stop();
            } catch (Exception e) {
                System.err.println("Failed to stop API Server: " + e.getMessage());
            }
        }
    }
}
