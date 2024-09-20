package net.peer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MessageSender implements Runnable {
    private final Socket socket;

    public MessageSender(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            while (!socket.isClosed()) {
                System.out.print("You: ");
                String message = scanner.nextLine();
                long sendTime = System.currentTimeMillis(); // Record the send time

                // Append the timestamp to the message
                out.println(message + "::" + sendTime);

                // Ensure the message is immediately flushed out
                out.flush();

                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Disconnecting...");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
}
