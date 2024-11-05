import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class PROFIClient {
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        String message = "SD2:Hello Server"; 
        byte[] buffer = message.getBytes();
        InetAddress address = InetAddress.getByName("localhost");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 3000);
        socket.setSoTimeout(5000);
        
        try {
            socket.send(packet);
            socket.receive(packet);
            System.out.println("Received: " + new String(packet.getData()).trim());
            socket.close();
        } catch (SocketTimeoutException e){
            System.out.println("Receive Timed Out");
        }
    }
}
