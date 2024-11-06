package Server.utils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.Set;

public class ShutdownHandler {
    public static void initiateGracefulShutdown(DatagramSocket socket, int delayMillis, Set<ClientInfo> clients, boolean running, boolean running2) throws IOException {
        try {
            // Inform the clients that the server is shutting down (optional step)
            BroadcastMessage.broadcastMessage("Server is shutting down. Goodbye!", socket, clients);

            // Delay for graceful shutdown
            for (int i = delayMillis / 1000; i > 0; i--) {
                System.out.println("Shutting down in " + i + " seconds...");
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Graceful shutdown interrupted.");
        } finally {
            // Change the running state to false
            running = false;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("Server process terminated.");
        }
    }
}
