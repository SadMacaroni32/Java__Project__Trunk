import java.io.IOException;
import java.net.*;
import java.util.*;

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
                initiateGracefulShutdown(socket, 5000); // 5-second delay
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
                        decryptedMessage = decryptAndVerify(receivedData); // Pass byte array
                    } catch (Exception e) {
                        System.err.println("Error decrypting message: " + e.getMessage());
                    }

                    if (decryptedMessage != null) {
                        String message = "Client [" + clInetAddress + ":" + clientPort + "]: " + decryptedMessage;
                        broadcastMessage(message, socket);
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

    private static void initiateGracefulShutdown(DatagramSocket socket, int delayMillis) {
        try {
            // Inform the clients that the server is shutting down (optional step)
            broadcastMessage("Server is shutting down. Goodbye!", socket);

            // Delay for graceful shutdown
            for (int i = delayMillis / 1000; i > 0; i--) {
                System.out.println("Shutting down in " + i + " seconds...");
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Graceful shutdown interrupted.");
        } finally {
            running = false;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("Server process terminated.");
        }
    }

    private static void broadcastMessage(String message, DatagramSocket socket) throws IOException {
        byte[] messageBytes = message.getBytes();

        for (ClientInfo client : clients) {
            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, client.getAddress(), client.getPort());
            socket.send(packet);
        }
    }

    private static String decryptAndVerify(byte[] data) throws Exception {
        byte[] encryptedMessage = Arrays.copyOfRange(data, 0, data.length - 32); // Assuming HMAC is 32 bytes
        byte[] receivedHmac = Arrays.copyOfRange(data, data.length - 32, data.length);

        if (!HMACUtil.verifyHMAC(encryptedMessage, receivedHmac)) {
            return null; // Invalid HMAC, discard the message
        }

        return EncryptionUtil.decrypt(encryptedMessage);
    }

    private static class ClientInfo {
        private final InetAddress address;
        private final int port;

        public ClientInfo(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }

        public InetAddress getAddress() {
            return address;
        }

        public int getPort() {
            return port;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ClientInfo that = (ClientInfo) obj;
            return port == that.port && Objects.equals(address, that.address);
        }

        @Override
        public int hashCode() {
            return Objects.hash(address, port);
        }

        @Override
        public String toString() {
            return address.toString() + ":" + port;
        }
    }
}
