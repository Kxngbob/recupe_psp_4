package com.monitor;

public class Main {

    public static void main(String[] args) throws Exception {

        // Create HTTP server on port 8080
        StatusHttpServer httpServer = new StatusHttpServer(8080);

        // Start HTTP server
        httpServer.start();

        // Create WebSocket server on port 9090
        MonitorWebSocketServer wsServer =
                new MonitorWebSocketServer(9090);

        // Start WebSocket server
        wsServer.start();

        // Show server information in console
        System.out.println("HTTP server running on: http://localhost:8080/api/status");
        System.out.println("WebSocket server running on: ws://localhost:9090");
    }
}