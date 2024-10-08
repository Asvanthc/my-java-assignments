package net.ftp;

import net.ftp.exceptions.InvalidCommandException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.ftp.handler.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class CommandParser {

    private static final CommandParser INSTANCE = new CommandParser();
    private static final Logger LOGGER = LogManager.getLogger(CommandParser.class);
    private static final int BUFFER_SIZE = 1024;

    private CommandParser() {
    }

    public static CommandParser getInstance() {
        return INSTANCE;
    }

    public Command readAndParseCommand(ByteBuffer buffer, SocketChannel clientChannel, SessionState sessionState)
            throws IOException, InvalidCommandException {


        while (buffer.remaining()>0) {
            byte b = buffer.get();
            sessionState.commandLine.append((char) b);

            // Check if the current character is a newline (command terminator)
            if (b == '\n') {
                String fullCommand = sessionState.commandLine.toString().trim();
                sessionState.commandLine=new StringBuilder();
                if(buffer.limit() == buffer.position()){
                    buffer.clear();
                    sessionState.setFlagRead(false);
                }
                buffer.compact().flip();
                return parse(fullCommand, clientChannel, sessionState);
            }

            if (sessionState.commandLine.length() >= BUFFER_SIZE) {
                LOGGER.warn("Command exceeded buffer size.");
                throw new InvalidCommandException("Command too long.");
            }
        }
        buffer.compact().flip();
        if(buffer.limit() == buffer.position()){
            buffer.clear();
            sessionState.setFlagRead(false);
        }

        return new Command() {
            @Override
            public String handle() {
                return "";
            }
        };
    }

    public Command parse(String commandLine, SocketChannel clientChannel, SessionState sessionState) throws InvalidCommandException {
        if (commandLine == null || commandLine.trim().isEmpty()) {
            throw new InvalidCommandException("Type some command, it cannot be empty.");
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

        return createCommand(command, argument, size, clientChannel, sessionState);
    }

    private Command createCommand(String command, String argument, int size, SocketChannel clientChannel, SessionState sessionState) throws InvalidCommandException {
        return switch (command) {
            case "PASS" -> new PassCommandHandler(argument);
            case "LIST" -> new ListCommandHandler(sessionState);
            case "PWD" -> new PwdCommandHandler(sessionState);
            case "CWD" -> new CwdCommandHandler(argument, sessionState);
            case "MKD" -> new MkdCommandHandler(argument);
            case "RMD" -> new RmdCommandHandler(argument);
            case "STOR" -> new StorCommandHandler(argument, size, sessionState);
            case "RETR" -> new RetrCommandHandler(argument, clientChannel, sessionState);
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
}
