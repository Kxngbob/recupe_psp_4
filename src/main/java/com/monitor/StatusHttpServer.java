package com.monitor;

import com.sun.management.OperatingSystemMXBean;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;

public class StatusHttpServer {

    // HTTP server object
    private HttpServer server;

    // Constructor
    public StatusHttpServer(int port) throws Exception {

        // Create HTTP server using selected port
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Create endpoint:
        // http://localhost:8080/api/status
        server.createContext("/api/status", this::handleStatus);
    }

    // Method to start server
    public void start() {
        server.start();
    }

    // Method executed when client accesses /api/status
    private void handleStatus(HttpExchange exchange) throws java.io.IOException {

        // Allow requests from browser (fix CORS issue)
        exchange.getResponseHeaders()
                .add("Access-Control-Allow-Origin", "*");

        // Tell browser response format is JSON
        exchange.getResponseHeaders()
                .add("Content-Type", "application/json");

        // Get system information object
        OperatingSystemMXBean osBean =
                (OperatingSystemMXBean)
                        ManagementFactory.getOperatingSystemMXBean();

        // Get operating system name
        String osName = System.getProperty("os.name");

        // Get processor architecture
        String arch = System.getProperty("os.arch");

        // Get total RAM memory
        long totalMemory = osBean.getTotalMemorySize();

        // Create JSON response
        String json = """
                {
                    "os": "%s",
                    "arch": "%s",
                    "totalMemory": %d
                }
                """.formatted(osName, arch, totalMemory);

        // Convert JSON string into bytes
        byte[] response = json.getBytes();

        // Send HTTP 200 OK response
        exchange.sendResponseHeaders(200, response.length);

        // Send JSON data to browser
        OutputStream output = exchange.getResponseBody();

        output.write(response);

        // Close stream
        output.close();
    }
}