package net.sock;

import java.io.*;
import java.net.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleEchoServer {
    private static final Logger logger = LogManager.getLogger(SimpleEchoServer.class);
    private static final int PORT = 12345;

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Server started on port " + PORT);
            acceptClients(serverSocket);
        } catch (IOException e) {
            logger.error("Error starting server: " + e.getMessage(), e);
        }
    }

    protected void acceptClients(ServerSocket serverSocket) {
        logger.info("Waiting for clients...");
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                logger.info("Client connected: " + clientSocket.getRemoteSocketAddress());
                handleClient(clientSocket);
            } catch (IOException e) {
                logger.error("Error accepting client: " + e.getMessage(), e);
            }
        }
    }

    protected void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            communicateWithClient(in, out);
        } catch (IOException e) {
            logger.error("Error in client communication: " + e.getMessage(), e);
        } finally {
            closeClientSocket(clientSocket);
        }
    }

    protected void communicateWithClient(BufferedReader in, PrintWriter out) throws IOException {
        String message;
        while ((message = receiveMessage(in)) != null) {
            String response = processMessage(message);
            sendMessage(out, response);
        }
    }

    protected String receiveMessage(BufferedReader in) throws IOException {
        String message = in.readLine();
        if (message != null) {
            logger.info("Received: " + message);
        }
        else
            message="test";
        return message;
    }

    protected String processMessage(String message) {
        return "Echo: " + message;
    }

    protected void sendMessage(PrintWriter out, String message) {
        logger.info("Sending: " + message);
        out.println(message);
    }

    protected void closeClientSocket(Socket clientSocket) {
        try {
            clientSocket.close();
            logger.info("Client socket closed.");
        } catch (IOException e) {
            logger.error("Error closing client socket: " + e.getMessage(), e);
        }
    }

    // Method to simulate processing delay
    private void simulateProcessingDelay() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Delay interrupted: " + e.getMessage(), e);
        }
    }
}
