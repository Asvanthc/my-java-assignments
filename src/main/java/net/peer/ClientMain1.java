package net.peer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain1 {
    private static final Logger logger = LogManager.getLogger(ClientMain1.class);
    private static volatile boolean running = true; // Control flag for client

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running = false;
            logger.info("Client is shutting down...");
        }));
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter server IP: ");
        String serverIp = scanner.nextLine();
        startClient(serverIp);
        scanner.close(); // Close the scanner
    }

    private static void startClient(String serverIp) {
        try (Socket socket = new Socket(serverIp, 5555)) {
            logger.info("Connected to server!");

            // Start threads for sending and receiving messages
            Thread senderThread = new Thread(new MessageSender(socket));
            Thread receiverThread = new Thread(new MessageReceiver(socket));

            senderThread.start();
            receiverThread.start();

            // Wait for the threads to finish
            while (running) {
                // Keep the main thread alive or perform other operations
            }

            // Ensure threads are interrupted if running is set to false
            senderThread.interrupt();
            receiverThread.interrupt();

        } catch (IOException e) {
            logger.error("Error connecting to server: ", e);
        }finally {
            logger.info("Client has stopped.");
        }
    }
}
