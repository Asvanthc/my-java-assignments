package net.ftp.exceptions;

public class InvalidCommandException extends FtpCommandException {
    // Assign a default error message and code
    private static final String DEFAULT_MESSAGE = "Invalid or unsupported command.";
    private static final String ERROR_CODE = "550";

    public InvalidCommandException() {
        super(DEFAULT_MESSAGE, ERROR_CODE);
    }

    public InvalidCommandException(String message) {
        super(message, ERROR_CODE);
    }
}
