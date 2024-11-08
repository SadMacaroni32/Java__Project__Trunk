package src.server;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class UDPServerHandler implements Runnable {
    private int port;
    private CopyOnWriteArrayList<InetSocketAddress> clientAddresses = new CopyOnWriteArrayList<>();

    public UDPServerHandler(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("UDP Server started on port: " + port);
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
                InetSocketAddress clientSocketAddress = new InetSocketAddress(clientAddress, clientPort);

                if (!clientAddresses.contains(clientSocketAddress)) {
                    clientAddresses.add(clientSocketAddress);
                }

                String clientName = "Unknown"; 
                String content = message; 
                
                String[] parts = message.split(":", 2);
                if (parts.length == 2) {
                    clientName = parts[0].trim();
                    content = parts[1].trim();
                }

                String formattedMessage = clientName + "/UDP/" + clientAddress.getHostAddress() + ": " + content;

                System.out.println(formattedMessage);

                for (InetSocketAddress addr : clientAddresses) {
                    if (!addr.equals(clientSocketAddress)) {
                        byte[] sendBuffer = formattedMessage.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, addr.getAddress(), addr.getPort());
                        socket.send(sendPacket);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
