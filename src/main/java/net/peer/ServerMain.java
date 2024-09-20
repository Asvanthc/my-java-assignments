package net.peer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    private static final Logger logger = LogManager.getLogger(ServerMain.class);
    private static volatile boolean running = true;

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running = false; // Set running to false to stop the server
            logger.info("Server is shutting down...");
        }));
        startServer();
    }

    private static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(5555)) {
            logger.info("Server is listening on port 5555...");

            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    logger.info("Client connected!");

                    // Start threads for sending and receiving messages
                    Thread senderThread = new Thread(new MessageSender(socket));
                    Thread receiverThread = new Thread(new MessageReceiver(socket));

                    senderThread.start();
                    receiverThread.start();

                    // Wait for the threads to finish
                    senderThread.join();
                    receiverThread.join();
                } catch (IOException e) {
                    logger.error("Error during client connection: ", e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupt status
                    logger.warn("Server interrupted: ", e);
                    break; // Exit the loop on interruption
                }
            }
        } catch (IOException e) {
            logger.fatal("Could not start server: ", e);
        }finally {
            logger.info("Server has stopped.");
        }
    }
}
