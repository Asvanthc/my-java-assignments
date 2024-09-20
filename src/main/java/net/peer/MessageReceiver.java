package net.peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MessageReceiver implements Runnable {
    private final Socket socket;

    public MessageReceiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;
            while (!socket.isClosed() && (message = in.readLine()) != null) {
                long receiveTime = System.currentTimeMillis(); // Record the receive time
                System.out.println("\nPeer: " + message);
                System.out.print("You: ");

                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Peer disconnected.");
                    break;
                }
                // Show the delay if the message contains a timestamp
                String[] parts = message.split("::");
                if (parts.length == 2) {
                    try {
                        long sendTime = Long.parseLong(parts[1]); // Extract the send time
                        long delay = receiveTime - sendTime; // Calculate the delay
                        System.out.println("[Delay: " + delay + " ms]");
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid timestamp format.");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error receiving message: " + e.getMessage());
        }
    }
}
