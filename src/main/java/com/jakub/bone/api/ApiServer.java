package com.jakub.bone.api;

import com.jakub.bone.database.Datasource;
import com.jakub.bone.server.ServerManger;
import com.jakub.bone.utills.DataImporter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ApiServer {
    public static void main(String[] args) throws Exception {
        Datasource datasource = new Datasource();
        DataImporter importer = new DataImporter(datasource);

        // Import SWIFT data
        importer.importSwiftRecords();

        // Init server
        ServerManger serverManager = new ServerManger(8080, datasource);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            serverManager.stopServer();
            datasource.getDatabaseSchema().truncateTable();
            datasource.closeConnection();
            log.info("Application closed successfully");
        }));

        serverManager.startServer();
    }
}
