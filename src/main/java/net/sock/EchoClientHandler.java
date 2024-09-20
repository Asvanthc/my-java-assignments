package net.sock;

import java.io.*;
import java.net.Socket;

public class EchoClientHandler extends Thread {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    public EchoClientHandler(Socket socket, BufferedReader in, PrintWriter out) {
        this.clientSocket = socket;
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                out.println(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
