package net.ftp;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class FtpClient {
    private static final String SERVER = "localhost"; // Change to server address if needed
    private static final int PORT = 2121;
    private static final int DELAY_MS = 2000; // Delay between each character (500 ms)

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER, PORT)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send commands with delay, simulating fragmented commands
            sendCommandWithDelay("LIST", out);

            // Receive and print the response from the server
            String response;
            System.out.println("Server Response: ");
            while ((response = in.readLine()) != null) {
                System.out.println(response);
                // Break out of the loop when the transfer is complete
                if (response.startsWith("226")) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendCommandWithDelay(String command, PrintWriter out) {
        char[] commandChars = command.toCharArray();
        for (char ch : commandChars) {
            out.print(ch); // Send one character at a time
            out.flush();
            System.out.println("Sent: " + ch);  // For debugging purposes, to show each character sent
            try {
                // Introduce a delay between each character
                TimeUnit.MILLISECONDS.sleep(DELAY_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Send newline after the full command
        out.print("\r\n");
        out.flush();
    }
}
