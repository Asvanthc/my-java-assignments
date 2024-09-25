package net.ftp.exceptions;

// Base exception for all FTP-related exceptions
public class FtpCommandException extends Exception {
    private final String errorCode;

    public FtpCommandException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
