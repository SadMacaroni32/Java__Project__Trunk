package src.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServerHandler implements Runnable {
    private int port;

    public UDPServerHandler(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (DatagramSocket datagramSocket = new DatagramSocket(port)) {
            System.out.println("UDP Server started on port: " + port);
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received UDP message: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
