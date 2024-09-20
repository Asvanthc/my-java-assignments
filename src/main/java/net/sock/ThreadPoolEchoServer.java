package net.sock;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadPoolEchoServer {
    private static final Logger logger = LogManager.getLogger(ThreadPoolEchoServer.class);
    private static final int MAX_THREADS = 10;
    private static final int PORT = 12345;

    // Start the server
    public void start() {
        ExecutorService threadPool = Executors.newFixedThreadPool(MAX_THREADS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("ThreadPoolEchoServer: Listening on port " + PORT);

            acceptClients(serverSocket, threadPool);
        } catch (IOException e) {
            logger.error("Server error: " + e.getMessage(), e);
        } finally {
            shutdownThreadPool(threadPool);
        }
    }

    // Accept clients and handle them in the thread pool
    protected void acceptClients(ServerSocket serverSocket, ExecutorService threadPool) {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                logger.info("Client connected from: " + clientSocket.getRemoteSocketAddress());
                threadPool.submit(new ClientHandler(clientSocket));
            } catch (IOException e) {
                logger.error("Error accepting client: " + e.getMessage(), e);
            }
        }
    }

    // Shutdown the thread pool
    protected void shutdownThreadPool(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
                if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    logger.error("ThreadPool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("ThreadPool successfully shut down");
    }

    // ClientHandler class to handle individual clients
    protected static class ClientHandler implements Runnable {
        private static final Logger logger = LogManager.getLogger(ClientHandler.class);
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                communicateWithClient(in, out);
            } catch (IOException e) {
                logger.error("Error handling client: " + e.getMessage(), e);
            } finally {
                closeClientSocket();
            }
        }

        // Communicate with the client (send/receive messages)
        protected void communicateWithClient(BufferedReader in, PrintWriter out) throws IOException {
            String message;
            while ((message = in.readLine()) != null) {
                logger.info("Received: " + message);
                out.println("Echo: " + message);
            }
        }

        // Close the client socket
        protected void closeClientSocket() {
            try {
                clientSocket.close();
                logger.info("Client socket closed.");
            } catch (IOException e) {
                logger.error("Error closing client socket: " + e.getMessage(), e);
            }
        }
    }
}
