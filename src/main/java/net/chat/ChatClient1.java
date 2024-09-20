package net.chat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ChatClient1 {
    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;
    private BufferedWriter writer;
    private BufferedReader reader;
    private Socket socket;

    public static void main(String[] args) {
        new ChatClient().start();
        new ChatClient().start();new ChatClient().start();
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
}
