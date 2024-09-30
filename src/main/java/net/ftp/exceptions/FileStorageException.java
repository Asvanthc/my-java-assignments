package net.ftp.exceptions;

// Custom exception class to handle storage-related issues
public class FileStorageException extends Exception {
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
