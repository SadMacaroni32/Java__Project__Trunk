import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public class PROFIServer {
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(3000);
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        
        while(true) {
            socket.setSoTimeout(5000);
            try{ 
                socket.receive(packet);
                String receivedData = new String(packet.getData()).trim();
                String response = handlePROFIBUSMessage(receivedData);
                packet.setData(response.getBytes());
                socket.send(packet);
            } catch (SocketTimeoutException e) {
                System.out.println("Receive Time Out. Awaiting Packet.");
            }
        }
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
