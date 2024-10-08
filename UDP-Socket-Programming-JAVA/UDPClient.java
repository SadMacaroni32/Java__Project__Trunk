import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class UDPClient {
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        String message = "Hello Server";
        byte[] buffer = message.getBytes();
        InetAddress address = InetAddress.getByName("localhost");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 3000);
        socket.setSoTimeout(5000);
        try {
        socket.send(packet);
        socket.receive(packet);
        System.out.println("Recieved: " + new String(packet.getData()));
        socket.close();
        } catch (SocketTimeoutException e){
            System.out.println("Recieve Timed Out");
        }
        
    }
}