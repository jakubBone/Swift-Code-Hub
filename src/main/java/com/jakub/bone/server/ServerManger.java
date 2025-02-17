package com.jakub.bone.server;

import com.jakub.bone.api.CountrySwiftCodeServlet;
import com.jakub.bone.api.SwiftCodeServlet;
import com.jakub.bone.database.DataSource;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

@Log4j2
public class ServerManger {
    private Server server;
    private ServletContextHandler context;
    private DataSource datasource;
    public ServerManger(int port, DataSource datasource) throws Exception {
        this.server = new Server(port);
        this.context = new ServletContextHandler();
        this.datasource = datasource;
        this.server.setHandler(context);
        this.context.setAttribute("datasource", datasource);

        // Init Servlets
        context.addServlet(new ServletHolder(new SwiftCodeServlet()), "/v1/swift-codes/*");
        context.addServlet(new ServletHolder(new CountrySwiftCodeServlet()), "/v1/swift-codes/country/*");
    }

    public void startServer() {
        try {
            server.start();
            log.info("Server is running");
            server.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("API Server interrupted", ex);
        } catch (Exception ex) {
            log.error("Failed to to start API Server. Error: {}", ex.getMessage(), ex);
        }
    }

    public void stopServer() {
        try {
            server.stop();
            datasource.getDatabaseSchema().truncateTable();
            log.info("Server stopped");
        } catch (Exception ex) {
            log.error("Failed to stop API Server. Error: {}", ex.getMessage(), ex);
        }
    }
}
