package net.ftp.handler;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import net.ftp.SessionState;
import net.ftp.exceptions.FileStorageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StorCommandHandler implements Command {

    private static final Logger LOGGER = LogManager.getLogger(StorCommandHandler.class);

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";

    private final String fileName;
    private final int size;
    private final SessionState sessionState;
    int bytesWritten = 0;
    private final ByteBuffer buffer;

    public StorCommandHandler(String fileName, int size, SessionState sessionState) {
        this.fileName = fileName;
        this.size = size;
        this.sessionState = sessionState;
        buffer = sessionState.getBuffer();// Use SS ByteBuffer
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
        try {
            if (!sessionState.isWritingFile())
                sessionState.setStore(this);
            writeFile(file);
            if (sessionState.isWritingFile()) return "";
            else return GREEN + "200 File transfer complete.\r\n" + RESET;
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

        Path path = Paths.get(sessionState.getCurrentDirectory() + "/" + fileName);
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

            LOGGER.info("Starting file write. Size: {}", size);

            int bytesToWrite = Math.min(buffer.remaining(), (size - bytesWritten));
            int limit = buffer.limit();
            buffer.limit(buffer.position() + bytesToWrite);
            bytesWritten += fileChannel.write(buffer);
            buffer.limit(limit);
            sessionState.setWritingFile(bytesWritten < size);


            if (bytesWritten != size) {
                throw new FileStorageException("File transfer Pending.. Expected: " + size +
                        " bytes, as of now wrote: " + bytesWritten, null);
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
