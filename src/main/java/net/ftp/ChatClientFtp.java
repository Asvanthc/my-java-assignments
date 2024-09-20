package net.ftp;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ChatClientFtp {
    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;
    private BufferedWriter writer;
    private BufferedReader reader;
    private Socket socket;

    public static void main(String[] args) {
        new ChatClientFtp().start();
    }

    public void start() {
        frame = new JFrame("Chat Client");
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
                sendMessage(textField.getText());
                textField.setText("");
            }
        });

        JButton sendFileButton = new JButton("Send File");
        sendFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendFile();
            }
        });

        frame.add(sendFileButton, BorderLayout.NORTH);

        new Thread(() -> {
            try {
                socket = new Socket("localhost", 12345);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                // Handle username input
                String username = JOptionPane.showInputDialog(frame, "Enter your username:");
                writer.write(username);
                writer.newLine();
                writer.flush();

                String message;
                while ((message = reader.readLine()) != null) {
                    textArea.append(message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void sendMessage(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (Socket fileSocket = new Socket("localhost", 12346);
                 FileInputStream fileInputStream = new FileInputStream(file);
                 OutputStream fileOutputStream = fileSocket.getOutputStream()) {

                // Send file name first
                writer.write("FILE:" + file.getName());
                writer.newLine();
                writer.flush();

                // Send file data
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }

                textArea.append("File " + file.getName() + " sent successfully.\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
