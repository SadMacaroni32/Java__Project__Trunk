package src.server;

import java.io.*;
import java.net.*;

public class TCPClientHandler implements Runnable {
    private int port;

    public TCPClientHandler(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("TCP Server started on port: " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new TCPClientHandlerThread(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class TCPClientHandlerThread implements Runnable {
    private Socket clientSocket;

    public TCPClientHandlerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received TCP message: " + message);
                out.println("Echo: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
