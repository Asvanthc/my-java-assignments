package net.ftp;

import net.ftp.exceptions.InvalidCommandException;
import net.ftp.handler.Command;
import net.ftp.handler.QuitCommandHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientNio {
    private static final Logger LOGGER = LogManager.getLogger(ClientNio.class);
    static String response;
    static ByteBuffer responseBuffer;

    static void handleClientRequest(SocketChannel clientChannel, SessionState sessionState) {
        ByteBuffer buffer = sessionState.getBuffer();
        try {

            while (buffer.hasRemaining() && sessionState.flagRead()) {

                if (sessionState.isBufferReadyForReading()) {
                    sessionState.bytesRead = clientChannel.read(buffer);
                    if (sessionState.bytesRead>0) {
                        buffer.flip();
                    }
                    if(sessionState.isWritingFile()){
                        response=sessionState.sch.handle();
                        responseBuffer = ByteBuffer.wrap(response.getBytes());
                        clientChannel.write(responseBuffer);
                    }
                    if (sessionState.bytesRead == -1) {
                        LOGGER.info("Client closed connection.");
                    }

                }

                try {
                    // Parse command from buffer
                    if(sessionState.flagRead()) {
                        Command cmd = CommandParser.getInstance().readAndParseCommand(buffer, clientChannel, sessionState);

                        if (cmd instanceof QuitCommandHandler || cmd == null) {
                            LOGGER.info("Client requested connection termination.");
                            closeClientChannel(clientChannel);
                            return;
                        }
                        // Handle the command
                        response = cmd.handle();
                        responseBuffer = ByteBuffer.wrap(response.getBytes());
                        clientChannel.write(responseBuffer);
                    }
                } catch (InvalidCommandException e) {
                    LOGGER.warn("Invalid command from client: ", e);
                    sendErrorResponse(clientChannel, e.getErrorCode() + " " + e.getMessage() + "\r\n");
                }

            }
        } catch (IOException e) {
            LOGGER.error("IO Exception while handling client request: ", e);
            closeClientChannel(clientChannel);
        }
    }



    private static void sendErrorResponse(SocketChannel clientChannel, String message) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
            clientChannel.write(buffer);
        } catch (IOException e) {
            LOGGER.error("Failed to send error response to client: ", e);
        }
    }

    private static void closeClientChannel(SocketChannel clientChannel) {
        try {
            if (clientChannel.isOpen()) {
                clientChannel.close();
                LOGGER.info("Closed client connection.");
            }
        } catch (IOException e) {
            LOGGER.error("Error while closing client channel: ", e);
        }
    }
}
