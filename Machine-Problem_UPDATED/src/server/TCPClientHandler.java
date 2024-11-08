package src.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class TCPClientHandler implements Runnable {
    private int port;
    private static CopyOnWriteArrayList<TCPClientHandlerThread> clientThreads = new CopyOnWriteArrayList<>();

    public TCPClientHandler(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("TCP Server started on port: " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                TCPClientHandlerThread clientThread = new TCPClientHandlerThread(clientSocket, clientThreads);
                clientThreads.add(clientThread);
                new Thread(clientThread).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class TCPClientHandlerThread implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private static CopyOnWriteArrayList<TCPClientHandlerThread> clientThreads;
    private String clientName;
    private InetAddress clientAddress;

    public TCPClientHandlerThread(Socket clientSocket, CopyOnWriteArrayList<TCPClientHandlerThread> clientThreads) {
        this.clientSocket = clientSocket;
        TCPClientHandlerThread.clientThreads = clientThreads;
        try {
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.clientAddress = clientSocket.getInetAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            clientName = in.readLine();  // Receive client name
            System.out.println(clientName + " connected from " + clientAddress.getHostAddress());

            String message;
            while ((message = in.readLine()) != null) {
                String formattedMessage = clientName + "/" + clientAddress.getHostAddress() + ": " + message;
                System.out.println(formattedMessage);  // Print the message on the server

                // Broadcast the message to all connected clients
                for (TCPClientHandlerThread clientThread : clientThreads) {
                    if (clientThread != this) {  // Don't send the message back to the sender
                        clientThread.sendMessage(formattedMessage);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Remove client from the list when they disconnect
            clientThreads.remove(this);
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(String message) {
        out.println(message);
    }
}
