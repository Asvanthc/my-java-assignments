package net.ftp;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class FtpServer {
    private static final int PORT = 2121;
    private static final int BUFFER_SIZE = 1024; // Reduced buffer size to handle incremental reads efficiently

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private String fileName;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received: " + inputLine);
                    String response = handleCommand(inputLine);
                    out.println(response);

                    if (response.startsWith("221")) {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String handleCommand(String command) {
            if (command.startsWith("PASS")) {
                return handlePassCommand(command);
            } else if (command.startsWith("LIST")) {
                return handleListCommand();
            } else if (command.startsWith("PWD")) {
                return handlePwdCommand();
            } else if (command.startsWith("CWD")) {
                return handleCwdCommand(command);
            } else if (command.startsWith("MKD")) {
                return handleMkdCommand(command);
            } else if (command.startsWith("RMD")) {
                return handleRmdCommand(command);
            } else if (command.startsWith("STOR")) {
                return handleStorCommand(command);
            } else if (command.startsWith("RETR")) {
                return handleRetrCommand(command);
            } else if (command.startsWith("QUIT")) {
                return "221 Goodbye.";
            } else {
                return "502 Command not implemented.";
            }
        }

        private String handlePassCommand(String command) {
            return "230 User logged in, proceed.";
        }

        // LIST Command - Listing the files and directories with file size calculation
        private String handleListCommand() {
            File currentDir = new File("server_files");
            String[] fileList = currentDir.list();
            int fileCount = (fileList != null) ? fileList.length : 0;

            if (fileList == null || fileCount == 0) {
                return "550 No files in directory.";
            }

            StringBuilder response = new StringBuilder("150 Here comes the directory listing.\n");
            long totalSize = 0;

            // Calculate the total size of all files in bytes
            for (String file : fileList) {
                File currentFile = new File(currentDir, file);
                if (currentFile.isFile()) {
                    totalSize += currentFile.length();
                }
                response.append(file).append("\n");
            }

            response.append("Total files listed: ").append(fileCount).append("\n");
            response.append("Total size of files: ").append(totalSize).append(" bytes\n");
            response.append("226 Transfer complete.\n");

            out.println("Total files listed: " + fileCount);  // Notify client of the total files
            out.println("Total size of files: " + totalSize + " bytes");
            return response.toString();
        }

        private String handlePwdCommand() {
            return "257 \"" + Paths.get("").toAbsolutePath().toString() + "\" is current directory.";
        }

        private String handleCwdCommand(String command) {
            String[] parts = command.split(" ", 2);
            if (parts.length < 2) return "503 Bad sequence of commands.";
            File dir = new File(parts[1]);
            if (dir.exists() && dir.isDirectory()) {
                System.setProperty("user.dir", dir.getAbsolutePath());
                return "250 Directory changed.";
            } else {
                return "550 No such directory.";
            }
        }

        private String handleMkdCommand(String command) {
            String[] parts = command.split(" ", 2);
            if (parts.length < 2) return "503 Bad sequence of commands.";
            File dir = new File(parts[1]);
            if (dir.mkdirs()) {
                return "257 Directory created.";
            } else {
                return "550 Directory creation failed.";
            }
        }

        private String handleRmdCommand(String command) {
            String[] parts = command.split(" ", 2);
            if (parts.length < 2) return "503 Bad sequence of commands.";
            File dir = new File(parts[1]);
            try {
                Files.walk(dir.toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                return "250 Directory removed.";
            } catch (IOException e) {
                e.printStackTrace();
                return "550 Directory removal failed.";
            }
        }

        // STOR Command - Handle file storage/upload
        private String handleStorCommand(String command) {
            String[] parts = command.split(" ", 2);
            if (parts.length < 2) return "503 Bad sequence of commands.";
            fileName = parts[1];
            return "150 File status okay. Ready to receive file.";
        }

        // RETR Command - Retrieve and send a file's contents with calculated file size
        private String handleRetrCommand(String command) {
            String[] parts = command.split(" ", 2);
            if (parts.length < 2) return "503 Bad sequence of commands.";
            fileName = parts[1];

            File file = new File(fileName);
            if (!file.exists()) {
                return "550 File not found.";
            }

            // Calculate the file size before sending
            long fileSize;
            try {
                fileSize = Files.size(file.toPath());
                out.println("File size: " + fileSize + " bytes");
            } catch (IOException e) {
                e.printStackTrace();
                return "550 Error getting file size.";
            }

            long sentBytes = 0;
            try (BufferedInputStream fileReader = new BufferedInputStream(new FileInputStream(file))) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = fileReader.read(buffer)) != -1 && sentBytes < fileSize) {
                    clientSocket.getOutputStream().write(buffer, 0, bytesRead);  // Send data to the client
                    sentBytes += bytesRead;
                }
                out.println("Total bytes sent: " + sentBytes);
                out.println("226 Transfer complete.\n");  // Inform client the transfer is complete
            } catch (IOException e) {
                e.printStackTrace();
                return "550 Error reading file.";
            }

            return "150 File status okay. Transferring " + fileName;
        }
    }
}
