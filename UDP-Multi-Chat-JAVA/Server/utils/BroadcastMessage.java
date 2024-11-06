package Server.utils;

import java.net.DatagramSocket;
import java.util.Set;
import java.io.IOException;
import java.net.DatagramPacket;

public class BroadcastMessage {
    public static void broadcastMessage(String message, DatagramSocket socket, Set<ClientInfo> clients) throws IOException {
        byte[] messageBytes = message.getBytes();

        for (ClientInfo client : clients) {
            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, client.getAddress(), client.getPort());
            socket.send(packet);
        }
    }
}