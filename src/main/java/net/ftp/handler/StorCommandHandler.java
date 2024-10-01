package net.ftp.handler;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import net.ftp.SessionState;
import net.ftp.exceptions.FileStorageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StorCommandHandler implements Command {

    private static final Logger LOGGER = LogManager.getLogger(StorCommandHandler.class);

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";

    private static final int BUFFER_SIZE = 1024;
    private final String fileName;
    private final SocketChannel clientChannel; // Changed to SocketChannel
    private final int size;
    private final SessionState sessionState;

    public StorCommandHandler(String fileName, SocketChannel clientChannel, int size, SessionState sessionState) {
        this.fileName = fileName;
        this.clientChannel = clientChannel; // Initialize SocketChannel
        this.size = size;
        this.sessionState = sessionState;
    }

    @Override
    public String handle() {
        if (fileName == null || fileName.trim().isEmpty()) {
            return RED + "500 Bad sequence of commands.\r\n" + RESET;
        }

        File file = new File(sessionState.getCurrentDirectory() + "/" + fileName);

        // Ensure parent directory exists, with custom error if creation fails
        try {
            createParentDirectories(file);
        } catch (FileStorageException e) {
            LOGGER.error("Failed to create directories for file: {}", file.getAbsolutePath(), e);
            return RED + "550 Unable to create directories.\r\n" + RESET;
        }

        // Try writing the file, catching specific errors like FileNotFoundException and IOException
        try {
            writeFile(file);
            return GREEN + "200 File transfer complete.\r\n" + RESET;
        } catch (FileStorageException e) {
            LOGGER.error("File storage error: {}", e.getMessage(), e);
            return RED + "550 " + e.getMessage() + "\r\n" + RESET;
        }
    }

    private void createParentDirectories(File file) throws FileStorageException {
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new FileStorageException("Failed to create parent directories", null);
            }
        }
    }

    private void writeFile(File file) throws FileStorageException {
        try (BufferedOutputStream fileOutput = new BufferedOutputStream(new FileOutputStream(file))) {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE); // Use ByteBuffer
            long bytesWritten = 0;

            LOGGER.info("Starting file write. Size: {}", size);

            while (bytesWritten < size) {
                int bytesRead = clientChannel.read(buffer); // Read from SocketChannel
                if (bytesRead == -1) {
                    throw new FileStorageException("Client closed connection during transfer.", null);
                }
                buffer.flip(); // Prepare buffer for writing

                // Write to the file from the buffer
                while (buffer.hasRemaining() && bytesWritten < size) {
                    fileOutput.write(buffer.get());
                    bytesWritten++;
                    LOGGER.debug("Written {} bytes so far.", bytesWritten);
                }
                buffer.clear(); // Clear buffer for next read
            }

            if (bytesWritten != size) {
                throw new FileStorageException("Incomplete file transfer. Expected: " + size + " bytes, but wrote: " + bytesWritten, null);
            }

        } catch (FileNotFoundException e) {
            throw new FileStorageException("File not found or cannot be created: " + file.getAbsolutePath(), e);
        } catch (IOException e) {
            throw new FileStorageException("Error during file storing for file: " + file.getAbsolutePath(), e);
        }
    }

    public String getFileName() {
        return fileName;
    }

    public int getSize() {
        return size;
    }
}
