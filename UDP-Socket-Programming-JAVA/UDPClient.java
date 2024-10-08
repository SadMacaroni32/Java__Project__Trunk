import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        String message = "Hello Server";
        byte[] buffer = message.getBytes();
        InetAddress address = InetAddress.getByName("localhost");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 3000);
        socket.send(packet);
        socket.receive(packet);
        System.out.println("Recieved: " + new String(packet.getData()));
        socket.close();
    }
}