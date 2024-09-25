package net.ftp.handler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import net.ftp.SessionState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StorCommandHandler implements Command {
    private static final Logger LOGGER = LogManager.getLogger(StorCommandHandler.class);
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";

    private static final int BUFFER_SIZE = 1024; // Fixed buffer size of 1 KB
    private final String fileName;
    private final InputStream clientInput;
    private final int size;

    // Constructor to accept the file name, client input stream, and size
    public StorCommandHandler(String fileName, InputStream clientInput, int size) {
        this.fileName = fileName;
        this.clientInput = clientInput;
        this.size = size;
    }

    @Override
    public String handle() {
        if (fileName == null || fileName.trim().isEmpty()) {
            return RED + "500 Bad sequence of commands.\r\n" + RESET;
        }

        File file = new File(SessionState.getInstance().getCurrentDirectory() + "/" + fileName);

        // Ensure the parent directory exists
        file.getParentFile().mkdirs();

        try (BufferedOutputStream fileOutput = new BufferedOutputStream(new FileOutputStream(file))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            long bytesWritten = 0; // Track how many bytes have been written
            int bytesRead;

            // Read from the client input stream in chunks and write to the file
            while (bytesWritten < size && (bytesRead = clientInput.read(buffer, 0, Math.min(buffer.length, size - (int)bytesWritten))) != -1) {
                fileOutput.write(buffer, 0, bytesRead);
                bytesWritten += bytesRead; // Update the count of written bytes
            }

            if (bytesWritten == size) {
                return GREEN + "200 File transfer complete.\r\n" + RESET; // Successful file transfer response
            } else {
                LOGGER.warn("File transfer incomplete. Expected size: {}, bytes written: {}", size, bytesWritten);
                return RED + "550 Incomplete file transfer.\r\n" + RESET;
            }

        } catch (IOException e) {
            LOGGER.error("IOE during file storing: ", e);
            return RED + "550 Error storing file.\r\n" + RESET; // Error response for file storage issues
        }
    }
    public String getFileName(){
        return fileName;
    }
    public int getSize(){
        return size;
    }
}
