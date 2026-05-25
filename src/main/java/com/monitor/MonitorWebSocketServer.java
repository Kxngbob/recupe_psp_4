package com.monitor;

import com.sun.management.OperatingSystemMXBean;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

public class MonitorWebSocketServer extends WebSocketServer {

    // Object used to read CPU information
    private OperatingSystemMXBean osBean;

    // Constructor
    public MonitorWebSocketServer(int port) {

        // Create WebSocket server using selected port
        super(new InetSocketAddress(port));

        // Initialize operating system bean
        osBean = (OperatingSystemMXBean)
                ManagementFactory.getOperatingSystemMXBean();

        // Timer used to repeat task every second
        Timer timer = new Timer();

        // Execute this task every 1000 milliseconds
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                // Send CPU usage to connected clients
                sendCpuUsage();
            }

        }, 0, 1000);
    }

    // Method that sends CPU percentage
    private void sendCpuUsage() {

        // Get CPU load value (0.0 -> 1.0)
        double cpuLoad = osBean.getCpuLoad() * 100;

        // Prevent negative values
        if (cpuLoad < 0) {
            cpuLoad = 0;
        }

        // Create JSON response
        String json = """
                {
                    "cpu": %.2f
                }
                """.formatted(cpuLoad);

        // Send JSON data to all connected clients
        broadcast(json);
    }

    // Executed when client connects
    @Override
    public void onOpen(WebSocket conn,
                       ClientHandshake handshake) {

        System.out.println("Client connected: "
                + conn.getRemoteSocketAddress());
    }

    // Executed when client disconnects
    @Override
    public void onClose(WebSocket conn,
                        int code,
                        String reason,
                        boolean remote) {

        System.out.println("Client disconnected");
    }

    // Executed when message is received from client
    @Override
    public void onMessage(WebSocket conn,
                          String message) {

        System.out.println("Message from client: "
                + message);
    }

    // Executed if error occurs
    @Override
    public void onError(WebSocket conn,
                        Exception ex) {

        System.out.println("WebSocket error: "
                + ex.getMessage());
    }

    // Executed when WebSocket server starts
    @Override
    public void onStart() {

        System.out.println("WebSocket server started");
    }
}