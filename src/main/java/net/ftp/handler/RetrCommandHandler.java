package net.ftp.handler;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
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
    private final SocketChannel clientChannel; // Changed to SocketChannel
    private final SessionState sessionState;

    // Constructor to accept the file name and socket channel
    public RetrCommandHandler(String fileName, SocketChannel clientChannel, SessionState sessionState) {
        this.fileName = fileName;
        this.clientChannel = clientChannel; // Initialize SocketChannel
        this.sessionState = sessionState;
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
            ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
            clientChannel.write(responseBuffer); // Write to the SocketChannel

            // Send the file content to the client
            try (var fileInput = Files.newInputStream(file.toPath())) {
                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                int bytesRead;

                LOGGER.info("Starting file transfer: {}", file.getAbsolutePath());

                while ((bytesRead = fileInput.read(buffer.array())) != -1) {
//                    buffer.limit(bytesRead); // Set limit to the number of bytes read
//                    buffer.flip(); // Prepare buffer for writing
                    while (buffer.hasRemaining()) {
                        clientChannel.write(buffer); // Write to the SocketChannel
                    }
//                    buffer.clear(); // Clear the buffer for the next read
                }

                LOGGER.info("File transfer complete: {}", file.getAbsolutePath());
            }

            return GREEN + "\n200 Transfer complete." + RESET + "\r\n";

        } catch (IOException e) {
            LOGGER.error("IOE during file retrieval: ", e);
            return "550 Error reading file.\r\n"; // Error response for file retrieval issues
        }
    }

    public String getFileName() {
        return fileName;
    }
}
