package net.ftp.exceptions;

public class CommandTooLongException extends FtpCommandException {
    // Assign a default error message and code
    private static final String DEFAULT_MESSAGE = "Command exceeds maximum length.";
    private static final String ERROR_CODE = "550";

    public CommandTooLongException() {
        super(DEFAULT_MESSAGE, ERROR_CODE);
    }

    public CommandTooLongException(String message) {
        super(message, ERROR_CODE);
    }
}
