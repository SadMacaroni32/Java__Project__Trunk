package src.client;

import src.utility.ConfigUtil;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Choose protocol (TCP/UDP): ");
        String protocol = scanner.nextLine().toUpperCase();
        
        String serverAddress = ConfigUtil.getProperty("server.address");
        int tcpPort = Integer.parseInt(ConfigUtil.getProperty("tcp.port"));
        int udpPort = Integer.parseInt(ConfigUtil.getProperty("udp.port"));
        
        System.out.println("Enter your name: ");
        String name = scanner.nextLine();

        try {
            if (protocol.equals("TCP")) {
                connectTCP(serverAddress, tcpPort, name);
            } else if (protocol.equals("UDP")) {
                connectUDP(serverAddress, udpPort, name);
            } else {
                System.out.println("Invalid protocol selected.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void connectTCP(String serverAddress, int port, String name) throws IOException {
        Socket socket = new Socket(serverAddress, port);
        System.out.println("Connected to the server via TCP");

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        
        out.println(name + "/TCP");
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String message;
                    while (true) {
                        message = new Scanner(System.in).nextLine();
                        out.println(message); 
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        
        String response;
        while ((response = in.readLine()) != null) {
            System.out.println(response); 
        }
    }
        
    private static void connectUDP(String serverAddress, int port, String name) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverInetAddress = InetAddress.getByName(serverAddress);
        
        System.out.println("Connected to the server via UDP");
    
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Scanner scanner = new Scanner(System.in);
                    while (true) {
                        String message = scanner.nextLine();
                        String formattedMessage = name + ": " + message;
                        byte[] msgBuffer = formattedMessage.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(msgBuffer, msgBuffer.length, serverInetAddress, port);
                        socket.send(sendPacket); 
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
