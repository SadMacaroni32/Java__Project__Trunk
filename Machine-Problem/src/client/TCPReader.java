package src.client;

import java.io.*;
import java.net.Socket;

public class TCPReader implements Runnable {
    private String address;
    private int port;

    public TCPReader(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(address, port);
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
            System.out.println("Connected to TCP server.");
            new Thread(new TCPReceiver(socket)).start();
            String userInput;
            while ((userInput = reader.readLine()) != null) {
                writer.println(userInput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class TCPReceiver implements Runnable {
    private Socket socket;

    public TCPReceiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Server: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
