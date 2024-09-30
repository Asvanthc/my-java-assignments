package net.ftp.handler;

import java.io.*;
import net.ftp.SessionState;
import net.ftp.exceptions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StorCommandHandler implements Command {

    private static final Logger LOGGER = LogManager.getLogger(StorCommandHandler.class);

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";

    private static final int BUFFER_SIZE = 1024;
    private final String fileName;
    private final InputStream clientInput;
    private final int size;
    private final SessionState sessionState;

    public StorCommandHandler(String fileName, InputStream clientInput, int size, SessionState sessionState) {
        this.fileName = fileName;
        this.clientInput = clientInput;
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
            byte[] buffer = new byte[BUFFER_SIZE];
            long bytesWritten = 0;
            int bytesRead;

            // Log start of file transfer
            LOGGER.info("Starting file write.  Size: {}", size);

            while (bytesWritten < size && (bytesRead = clientInput.read(buffer, 0, Math.min(buffer.length, size - (int) bytesWritten))) != -1) {
                fileOutput.write(buffer, 0, bytesRead);
                bytesWritten += bytesRead;
                LOGGER.debug("Written {} bytes so far.", bytesWritten);
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
