package net.ftp;

import net.ftp.exceptions.InvalidCommandException;
import net.ftp.handler.Command;
import net.ftp.handler.ContinueNewRead;
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
            if(buffer.limit()==buffer.position()) sessionState.getBuffer().clear();

            sessionState.bytesRead = clientChannel.read(sessionState.getBuffer());
            if (sessionState.bytesRead>0) {
                sessionState.getBuffer().flip();
            }

            while (buffer.hasRemaining()) {
                // Continue read and write file
                if (sessionState.isWritingFile()) {
                    response = sessionState.sch.handle();
                } else {
                    Command cmd = CommandParser.getInstance().readAndParseCommand(buffer, clientChannel, sessionState);
                    if (cmd instanceof QuitCommandHandler) {
                        LOGGER.info("Client requested connection termination.");
                        closeClientChannel(clientChannel);
                        return;
                    }
                    if(cmd instanceof ContinueNewRead) break;

                    response = cmd.handle();
                }

                if (buffer.remaining() > 0 ) buffer.compact();
                else buffer.compact().flip();
                responseBuffer = ByteBuffer.wrap(response.getBytes());
                clientChannel.write(responseBuffer);
            }
        } catch (InvalidCommandException e) {
            LOGGER.warn("Invalid command from client: ", e);
            sendErrorResponse(clientChannel, e.getErrorCode() + " " + e.getMessage() + "\r\n");
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
