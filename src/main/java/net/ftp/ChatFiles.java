package net.ftp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatFiles {
    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;
    private ExecutorService clientHandlers;
    private ServerSocket serverSocket;
    private final Map<UUID, String> clientUsernames = new HashMap<>();
    private static final int PORT = 12345;
    private static final int FILE_PORT = 12346;

    public static void main(String[] args) {
        new ChatFiles().start();
    }

    public void start() {
        frame = new JFrame("Chat Server");
        textArea = new JTextArea();
        textField = new JTextField();

        textArea.setEditable(false);
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.add(textField, BorderLayout.SOUTH);
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                broadcastMessage("Server: " + textField.getText());
                textField.setText("");
            }
        });

        clientHandlers = Executors.newFixedThreadPool(10);
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                textArea.append("Server started...\n");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    clientHandlers.submit(new ClientHandler(clientSocket, this));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void broadcastMessage(String message) {
        textArea.append(message + "\n");
        ClientHandler.broadcastMessage(message);
    }

    public void appendTextArea(String message) {
        SwingUtilities.invokeLater(() -> textArea.append(message + "\n"));
    }

    public void addClient(UUID clientId, String username) {
        synchronized (clientUsernames) {
            clientUsernames.put(clientId, username);
        }
    }

    public void removeClient(UUID clientId) {
        synchronized (clientUsernames) {
            clientUsernames.remove(clientId);
        }
    }

    public String getUsername(UUID clientId) {
        synchronized (clientUsernames) {
            return clientUsernames.getOrDefault(clientId, "Client");
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedWriter writer;
        private BufferedReader reader;
        private UUID clientId;
        private ChatFiles server;
        private static final List<ClientHandler> clients = new ArrayList<>();
        private static final int FILE_PORT = 12346;

        public ClientHandler(Socket socket, ChatFiles server) {
            this.socket = socket;
            this.server = server;
            this.clientId = UUID.randomUUID(); // Unique identifier for each client
            synchronized (clients) {
                clients.add(this);
            }
        }

        @Override
        public void run() {
            try {
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Request username from client
                writer.write("Start Texting: ");
                writer.newLine();
                writer.flush();

                String username = reader.readLine();
                server.addClient(clientId, username);

                server.appendTextArea(username + " has joined the chat.");

                String message;
                while ((message = reader.readLine()) != null) {
                    if (message.startsWith("FILE:")) {
                        handleFileTransfer(message);
                    } else {
                        server.appendTextArea(username + ": " + message);
                        broadcastMessage(username + ": " + message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clients) {
                    clients.remove(this);
                }
                server.removeClient(clientId);
                server.appendTextArea(server.getUsername(clientId) + " has left the chat.");
            }
        }

        private void handleFileTransfer(String message) throws IOException {
            // Message format: FILE:filename
            String[] parts = message.split(":", 2);
            if (parts.length == 2) {
                String fileName = parts[1];
                server.appendTextArea("Receiving file: " + fileName);

                // Prepare to receive the file
                try (ServerSocket fileServerSocket = new ServerSocket(FILE_PORT);
                     Socket fileSocket = fileServerSocket.accept();
                     InputStream fileInputStream = fileSocket.getInputStream();
                     FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                }

                server.appendTextArea("File " + fileName + " received successfully.");
                broadcastMessage("Server received file: " + fileName);
            }
        }

        private static void broadcastMessage(String message) {
            synchronized (clients) {
                for (ClientHandler client : clients) {
                    try {
                        client.writer.write(message);
                        client.writer.newLine();
                        client.writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
