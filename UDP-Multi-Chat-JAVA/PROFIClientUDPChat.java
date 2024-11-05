import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class PROFIClientUDPChat {
    private static DatagramSocket socket; // Declare socket as a static member variable
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        String serverAddressStr = (args.length > 0) ? args[0] : "127.0.0.1";
        int serverPort = (args.length > 1) ? Integer.parseInt(args[1]) : 3000;

        try {
            socket = new DatagramSocket(); // Initialize the socket here
            InetAddress serverAddress = InetAddress.getByName(serverAddressStr);
            byte[] buffer = new byte[BUFFER_SIZE];

            System.out.println("Connected to chat server. Type your messages below:");

            // Start a thread to receive messages
            Thread receiveThread = new Thread(() -> receiveMessages(buffer));
            receiveThread.start();

            // Main thread for sending messages
            sendMessages(serverAddress, serverPort);

        } catch (IOException e) {
            System.err.println("Error during chat client operation: " + e.getMessage());
        } finally {
            closeSocket();
        }
    }

    private static void receiveMessages(byte[] buffer) {
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); // Blocking call until a packet is received
                String message = new String(packet.getData(), 0, packet.getLength()).trim();
                System.out.println("Received: " + message);
            } catch (IOException e) {
                System.out.println("Error receiving message: " + e.getMessage());
                break; // Exit on error
            }
        }
    }

    private static void sendMessages(InetAddress serverAddress, int serverPort) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String message = scanner.nextLine(); // Read user input
                if (message.equalsIgnoreCase("exit")) {
                    break; // Exit on "exit" command
                }
    
                byte[] encryptedMessage;
                try {
                    // Encrypt the message
                    encryptedMessage = EncryptionUtil.encrypt(message); // Handle potential exception
                } catch (Exception e) {
                    System.out.println("Error during encryption: " + e.getMessage());
                    continue; // Skip sending this message
                }
    
                byte[] hmac;
                try {
                    hmac = HMACUtil.generateHMAC(encryptedMessage); // Handle potential exception
                } catch (Exception e) {
                    System.out.println("Error generating HMAC: " + e.getMessage());
                    continue; // Skip sending this message
                }
    
                // Combine the encrypted message and HMAC
                byte[] messageToSend = new byte[encryptedMessage.length + hmac.length];
                System.arraycopy(encryptedMessage, 0, messageToSend, 0, encryptedMessage.length);
                System.arraycopy(hmac, 0, messageToSend, encryptedMessage.length, hmac.length);
    
                // Send the packet to the server
                try {
                    DatagramPacket packet = new DatagramPacket(messageToSend, messageToSend.length, serverAddress, serverPort);
                    socket.send(packet);
                } catch (IOException e) {
                    System.out.println("Error sending message: " + e.getMessage());
                }
            }
        }
    }
    

    private static void closeSocket() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("Disconnected from server.");
        }
    }
}
