package net.ftp.handler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import net.ftp.SessionState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RetrCommandHandler implements Command {
    private static final Logger LOGGER = LogManager.getLogger(RetrCommandHandler.class);
    private static final int BUFFER_SIZE = 1024;
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";

    private final String fileName;
    private final OutputStream out;
    private SessionState sessionState;

    // Constructor to accept the file name and output stream
    public RetrCommandHandler(String fileName, OutputStream out, SessionState sessionState) {
        this.fileName = fileName;
        this.out = out;
        this.sessionState=sessionState;
    }

    @Override
    public String handle() {
        if (fileName == null || fileName.trim().isEmpty()) {
            return RED + "500 Bad sequence of commands." + RESET + "\r\n";
        }
        String currentDirectory = sessionState.getCurrentDirectory();
        File file = new File(currentDirectory, fileName);
        if (!file.exists()) {
            return RED + "404 File not found." + RESET + "\r\n";
        }

        try {
            long fileSize = Files.size(file.toPath());
            String response = YELLOW + "150 File status okay.\nTransferring.... " + fileSize + RESET + "\n";

            // Send the status response to the client
            out.write(response.getBytes());
            out.flush(); // Ensure the response is sent before file transfer

            // Send the file content to the client
            try (BufferedInputStream fileInput = new BufferedInputStream(new FileInputStream(file))) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;

                while ((bytesRead = fileInput.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                out.flush(); // Ensure all data is sent
            }

            return GREEN + "\n200 Transfer complete." + RESET + "\r\n";

        } catch (IOException e) {
            LOGGER.error("IOE during file retrieval: ", e);
            return "550 Error reading file.\r\n"; // Error response for file retrieval issues
        }
    }

    public String getFileName(){
        return fileName;
    }

}
