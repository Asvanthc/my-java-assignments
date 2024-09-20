package net.sock;
import java.io.*;
import java.net.*;

public class SimpleEchoClient {
    private String serverHost;
    private int serverPort;

    public SimpleEchoClient(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
    }

    public void start() {
        try (Socket socket = new Socket(serverHost, serverPort);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to server. Type your message (type 'exit' to quit):");

            String userMessage;
            while (true) {
                System.out.print("Enter message: ");
                userMessage = userInput.readLine();

                if ("exit".equalsIgnoreCase(userMessage)) {
                    System.out.println("Closing connection...");
                    break;
                }

                // Send user message to server
                out.println(userMessage);

                // Get server's response
                String response = in.readLine();
                System.out.println("Server response: " + response);
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SimpleEchoClient client = new SimpleEchoClient("localhost", 12345);
        client.start();
    }
}