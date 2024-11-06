package Server;

import java.io.IOException;
import java.net.*;
import java.util.*;

import Server.utils.ClientInfo;
import Server.utils.DecryptVerify;
import Server.utils.ShutdownHandler;
import Server.utils.BroadcastMessage;  // Ensure this is imported

public class PROFIServerUDPChat {

    private static Set<ClientInfo> clients = new HashSet<>();
    private static boolean running = true;

    public static void main(String[] args) {
        int serverPort = 3000;
        int bufferSize = 1024;

        try {
            DatagramSocket socket = new DatagramSocket(serverPort);
            byte[] buffer = new byte[bufferSize];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // Register a shutdown hook for graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nShutdown signal received. Initiating graceful shutdown...");
                try {
                    ShutdownHandler.initiateGracefulShutdown(socket, 5000, clients, true, running); // Pass running here
                } catch (IOException e) {
                    System.err.println("Error during shutdown: " + e.getMessage());
                }
            }));

            System.out.println("Chat server is running on port " + serverPort + "..");

            while (running) {
                try {
                    socket.receive(packet);
                    byte[] receivedData = Arrays.copyOf(packet.getData(), packet.getLength()); // Get data as byte array

                    InetAddress clInetAddress = packet.getAddress();
                    int clientPort = packet.getPort();
                    ClientInfo clientInfo = new ClientInfo(clInetAddress, clientPort);

                    if (!clients.contains(clientInfo)) {
                        clients.add(clientInfo);
                        System.out.println("New client joined: " + clientInfo);
                    }

                    // Handle the decryptAndVerify method call
                    String decryptedMessage = null;
                    try {
                        decryptedMessage = DecryptVerify.decryptAndVerify(receivedData); // Pass byte array
                    } catch (Exception e) {
                        System.err.println("Error decrypting message: " + e.getMessage());
                    }

                    if (decryptedMessage != null) {
                        String message = "Client [" + clInetAddress + ":" + clientPort + "]: " + decryptedMessage;
                        BroadcastMessage.broadcastMessage(message, socket, clients);  // Make sure to pass clients to broadcastMessage method
                    } else {
                        System.out.println("Invalid message received from " + clientInfo);
                    }

                } catch (SocketTimeoutException e) {
                    System.out.println("Receive timeout. Waiting for new packets..");
                } catch (SocketException e) {
                    if (!running) {
                        System.out.println("Socket closed as part of graceful shutdown.");
                    } else {
                        throw e; // Rethrow if it's an unexpected error
                    }
                }
            }

            socket.close();
            System.out.println("Server socket closed.");
        } catch (IOException e) {
            System.err.println("Error initializing server: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format for port or buffer size.");
        }
    }
}
