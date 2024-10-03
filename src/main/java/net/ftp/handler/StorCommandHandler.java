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
    private ByteBuffer buffer1= ByteBuffer.allocate(1024);//for common writing

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
            ByteBuffer buffer = sessionState.getBuffer(); // Use ByteBuffer
            long bytesWritten = 0;

            LOGGER.info("Starting file write. Size: {}", size);

            while (bytesWritten < size) {
                if (buffer.hasRemaining() && (buffer.limit()-buffer.position())>size && (buffer.position()!=0)){
                    byte[] subsetArray = new byte[size];
                    buffer.get(subsetArray, 0, size);
                    buffer1=ByteBuffer.wrap(subsetArray);
                    buffer.mark();
                    // Reset the buffer to the marked position
                    buffer.reset();
                    // Skip the newline character
                    buffer.position(buffer.position());
                    if(buffer.limit()==buffer.position()){
                        buffer.clear();
                    }
                }

                if(buffer.hasRemaining() && (buffer.limit()-buffer.position())<size){
                    buffer1=ByteBuffer.allocate(buffer.remaining());
                    buffer.clear();
                }

                if(buffer.position()==0) {
                    int bytesRead = clientChannel.read(buffer1); // Read from SocketChannel
                    buffer1.flip(); // Prepare buffer for writing
                    if (bytesRead == -1) {
                        throw new FileStorageException("Client closed connection during transfer.", null);
                    }
                }

                // Write to the file from the buffer
                while (buffer1.hasRemaining() && bytesWritten < size) {
                    fileOutput.write(buffer1.get());
                    bytesWritten++;
                    LOGGER.debug("Written {} bytes so far.", bytesWritten);
                }
                while(buffer1.hasRemaining()) {
                     // Prepare buffer for reading from the beginning
                    byte b=buffer1.get();
                    buffer.put(b);

                }
                buffer.flip();
                buffer1.clear(); // Clear buffer for next read
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

    public ByteBuffer getBuffer1() {
        return buffer1;
    }

    public void setBuffer1(ByteBuffer buffer1) {
        this.buffer1 = buffer1;
    }
}
