package src.client;

import src.utility.ConfigUtil;

public class ChatClient {
    public static void main(String[] args) {
        String serverAddress = ConfigUtil.getProperty("server.address");
        int tcpPort = Integer.parseInt(ConfigUtil.getProperty("tcp.port"));
        int udpPort = Integer.parseInt(ConfigUtil.getProperty("udp.port"));

        new Thread(() -> startTCPClient(serverAddress, tcpPort)).start();
        new Thread(() -> startUDPClient(serverAddress, udpPort)).start();
    }

    private static void startTCPClient(String address, int port) {
        TCPReader tcpReader = new TCPReader(address, port);
        tcpReader.run();
    }

    private static void startUDPClient(String address, int port) {
        UDPClientHandler udpHandler = new UDPClientHandler(address, port);
        udpHandler.run();
    }
}
