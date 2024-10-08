import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public class UDPServer {
    /* Open Terminal type 'javac UDPClient.java
        Run UDPServer.java
        Terminal, type 'java UDPClient' */
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(3000);
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        
        while(true) {
            socket.setSoTimeout(5000);
            try{ 
            socket.receive(packet);
            System.out.println("Recieved: " + new String(packet.getData()));
            socket.send(packet);
            } catch (SocketTimeoutException e) {
                System.out.println("Recieved Time Out. Awaiting Packet.");
            }
        }
    }
}