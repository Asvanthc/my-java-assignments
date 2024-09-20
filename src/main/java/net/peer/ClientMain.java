package net.peer;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter server IP: ");
        String serverIp = scanner.nextLine();
        startClient(serverIp);
    }

    // Client Side
    private static void startClient(String serverIp) {
        try (Socket socket = new Socket(serverIp, 5555)) {
            System.out.println("Connected to server!");

            // Start threads for sending and receiving messages
            Thread senderThread = new Thread(new MessageSender(socket));
            Thread receiverThread = new Thread(new MessageReceiver(socket));

            senderThread.start();
            receiverThread.start();

            // Wait for the threads to finish
            senderThread.join();
            receiverThread.join();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
