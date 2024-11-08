package src.server;

import src.utility.ConfigUtil;

public class ChatServer {
    public static void main(String[] args) {
        int tcpPort = Integer.parseInt(ConfigUtil.getProperty("tcp.port"));
        int udpPort = Integer.parseInt(ConfigUtil.getProperty("udp.port"));

        new Thread(() -> {
            try {
                startTCPServer(tcpPort);
            } catch (Exception e) {
                System.err.println("Error starting TCP server: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                startUDPServer(udpPort);
            } catch (Exception e) {
                System.err.println("Error starting UDP server: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private static void startTCPServer(int port) {
        TCPClientHandler tcpHandler = new TCPClientHandler(port);
        new Thread(tcpHandler).start();  // Ensure it runs on a separate thread.
    }

    private static void startUDPServer(int port) {
        UDPServerHandler udpHandler = new UDPServerHandler(port);
        new Thread(udpHandler).start();  // Ensure it runs on a separate thread.
    }
}
