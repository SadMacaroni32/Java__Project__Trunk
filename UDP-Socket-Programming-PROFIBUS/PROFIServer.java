import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public class PROFIServer {
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(3000);
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        boolean running = true; // Add a flag to control the loop
        
        while(running) {
            socket.setSoTimeout(5000);
            try {
                socket.receive(packet);
                String receivedData = new String(packet.getData(), 0, packet.getLength()).trim();

                // Check for a "CLOSE" command to stop the server
                if (receivedData.equalsIgnoreCase("CLOSE")) {
                    System.out.println("Closing server...");
                    running = false; // Exit the loop
                    continue; // Skip sending a response
                }

                String response = handlePROFIBUSMessage(receivedData);
                packet.setData(response.getBytes());
                packet.setLength(response.length()); // Set the correct length for the response
                socket.send(packet);
            } catch (SocketTimeoutException e) {
                System.out.println("Receive Time Out. Awaiting Packet.");
            }
        }
        
        socket.close(); // Close the socket outside the loop
        System.out.println("Server socket closed.");
    }

    private static String handlePROFIBUSMessage(String data) {
        if (data.startsWith("SD1")) {
            return "SD1 Acknowledged";
        } else if (data.startsWith("SD2")) {
            return "SD2 Data Processed";
        } else if (data.startsWith("SD4")) {
            return "SD4 Token Received";
        } else if (data.startsWith("SC")) {
            return "SC Short Answer";
        } else {
            return "Unknown Message Type";
        }
    }
}
