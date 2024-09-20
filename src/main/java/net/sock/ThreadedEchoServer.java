package net.sock;
import java.io.*;
import java.net.*;

public class ThreadedEchoServer {
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("ThreadedEchoServer: Listening on port 12345");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");

                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);
                    out.println("Echo: " + message);
                }

            } catch (IOException e) {
                System.out.println("Error handling client: " + e.getMessage());
            }
        }
    }
}
