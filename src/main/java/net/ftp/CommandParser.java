package net.ftp;

import net.ftp.exceptions.InvalidCommandException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.ftp.handler.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CommandParser {

    private static final CommandParser INSTANCE = new CommandParser();
    private static final Logger LOGGER = LogManager.getLogger(CommandParser.class);
    private static final int BUFFER_SIZE = 1024;  // Larger buffer size for more efficient reading
    private static final int MARK_LIMIT = 1024;  // Must match buffer size to ensure full reset capability


    private CommandParser() {

    }

    public static CommandParser getInstance() {
        return INSTANCE;
    }


    public Command readAndParseCommand(InputStream in, OutputStream out, SessionState sessionState) throws IOException, InvalidCommandException {
        byte[] buffer = new byte[BUFFER_SIZE];
        StringBuilder commandLine = new StringBuilder();

        // Mark the stream with the same size as the buffer
        in.mark(MARK_LIMIT);

        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            // Convert the read bytes to a string

            String chunk = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
            commandLine.append(chunk);

            // Check if the chunk contains a newline character (command terminator)
            int newlineIndex = chunk.indexOf('\n');
            if (newlineIndex != -1) {
                // Process the command up to the newline character
                String fullCommand = commandLine.substring(0, commandLine.length() - (chunk.length() - newlineIndex)).trim();

                // Reset the stream position to avoid consuming excess data beyond the command
                in.reset();
                //noinspection ResultOfMethodCallIgnored
                in.skip(newlineIndex + 1);  // Skip past the newline character

                return parse(fullCommand, in, out, sessionState);  // Pass output stream to parse
            }

            // Re-mark the stream in case we need to reset later in the next chunk
            in.mark(MARK_LIMIT);
        }

        // If we reach here, it means the client has closed the connection
        return null;
    }

    public Command parse(String commandLine, InputStream in, OutputStream out, SessionState sessionState) throws InvalidCommandException {
        if (commandLine == null || commandLine.trim().isEmpty()) {
            throw new InvalidCommandException("Type Some command..cannot be empty");
        }

        // Normalize spaces and trim leading/trailing whitespace
        commandLine = commandLine.trim().replaceAll("\\s+", " ");

        // Split command into tokens
        String[] tokens = commandLine.split(" ");
        if (tokens.length == 0) {
            throw new InvalidCommandException();
        }

        String command = tokens[0].toUpperCase();
        String argument = tokens.length > 1 ? tokens[1] : null;
        int size = tokens.length > 2 ? parseSize(tokens[2]) : -1;  // Get size if available

        return createCommand(command, argument, size, in, out, sessionState);
    }

    private Command createCommand(String command, String argument, int size, InputStream in, OutputStream out, SessionState sessionState) throws InvalidCommandException {
        return switch (command) {
            case "PASS" -> new PassCommandHandler(argument);
            case "LIST" -> new ListCommandHandler(sessionState);
            case "PWD" -> new PwdCommandHandler(sessionState);
            case "CWD" -> new CwdCommandHandler(argument, sessionState);
            case "MKD" -> new MkdCommandHandler(argument);
            case "RMD" -> new RmdCommandHandler(argument);
            case "STOR" -> new StorCommandHandler(argument, in, size, sessionState);
            case "RETR" -> new RetrCommandHandler(argument, out, sessionState);
            case "QUIT" -> new QuitCommandHandler();
            default -> throw new InvalidCommandException("Invalid or unsupported command: " + command);
        };
    }

    private int parseSize(String sizeToken) {
        try {
            return Integer.parseInt(sizeToken);
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid size format: {}", sizeToken);
            return -1;
        }
    }

    public Command readAndParseCommand(com.DataTransfer dataTransfer, SessionState sessionState) throws IOException, InvalidCommandException {

        return null;
    }

}
