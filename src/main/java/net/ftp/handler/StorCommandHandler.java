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
    private final int size;
    private final SessionState sessionState;
    int bytesWritten = 0;

    public StorCommandHandler(String fileName, int size, SessionState sessionState) {
        this.fileName = fileName;
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
            if(!sessionState.isWritingFile()) sessionState.setStore(this);

            writeFile(file);

            if(sessionState.isWritingFile()) return "";
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
        try (FileOutputStream fileOutput = new FileOutputStream(file, true)) {
            ByteBuffer buffer = sessionState.getBuffer(); // Use ByteBuffer


            LOGGER.info("Starting file write. Size: {}", size);

           /* while (bytesWritten < size) {

                //break for new reading
                if (sessionState.isBufferReadyForReading()) {
                    sessionState.setWritingFile(true);
                    break;
                }

                if (sessionState.checkBuffer()) {
                    int bytesToWrite = Math.min(buffer.remaining(), (size - bytesWritten));
                    byte[] subsetArray = new byte[bytesToWrite];
                    buffer.get(subsetArray, 0, bytesToWrite);
                    fileOutput.write(subsetArray); // Append the new data
                    bytesWritten += bytesToWrite;
                    buffer.compact();
                    buffer.flip();
                }
                else if (sessionState.check1Buffer()) {
                    byte[] subsetArray = new byte[buffer.limit()];
                    buffer.get(subsetArray, 0, buffer.limit());
                    fileOutput.write(subsetArray);// Append the new data
                    bytesWritten += buffer.limit();
                    buffer.clear();
                    sessionState.setFlagRead(false);
                }



            }*/

            int bytesToWrite = Math.min(buffer.remaining(), (size - bytesWritten));
            if ( buffer.remaining()>0 && !sessionState.isBufferReadyForReading() ) {
                fileOutput.write(buffer.array(),buffer.position(),bytesToWrite);
                bytesWritten += bytesToWrite;
            }
            if (bytesWritten < size) {
                sessionState.setWritingFile(true);
                sessionState.setFlagRead(false);
                buffer.clear();
            }

            else{
                buffer.position(bytesToWrite);
                sessionState.setWritingFile(false);
            }
            buffer.compact().flip();

            if (bytesWritten != size) {
                throw new FileStorageException("File transfer Pending.. Expected: " + size + " bytes, as of now wrote: " + bytesWritten, null);
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
