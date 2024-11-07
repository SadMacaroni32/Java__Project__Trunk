package src.client;

import java.io.*;
import java.net.*;

public class UDPClientHandler implements Runnable {
    private String address;
    private int port;

    public UDPClientHandler(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        try (DatagramSocket datagramSocket = new DatagramSocket();
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("UDP Client ready. Type a message:");
            String userInput;
            while ((userInput = reader.readLine()) != null) {
                byte[] buffer = userInput.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(address), port);
                datagramSocket.send(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
