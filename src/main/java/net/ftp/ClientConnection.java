package net.ftp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import net.ftp.handler.*;
import net.ftp.exceptions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientConnection implements Runnable {
    private final SocketChannel clientChannel;
    private static final Logger LOGGER = LogManager.getLogger(ClientConnection.class);
    private final SessionState sessionState;
    ByteBuffer buffer1= ByteBuffer.allocate(1024);//for common writing

    public ClientConnection(SocketChannel clientChannel) {
        this.clientChannel = clientChannel;
        this.sessionState = new SessionState();
    }

    @Override
    public void run() {
        ByteBuffer buffer = sessionState.getBuffer();

        boolean isConnectionActive = true;

        try {
            while (isConnectionActive) {
                Command cmd = CommandParser.getInstance().readAndParseCommand(buffer, clientChannel, sessionState);
                String response = cmd.handle();
                buffer1.clear();
                buffer1.put(response.getBytes());
                buffer1.flip();
                 clientChannel.write(buffer1);

                if (cmd instanceof QuitCommandHandler) {
                    LOGGER.info("Client requested connection termination.");
                    isConnectionActive = false;
                }
            }
        } catch (IOException e) {
            LOGGER.error("IO Exception in client connection: ", e);
        } catch (InvalidCommandException e) {
            LOGGER.warn("Invalid cmd client connection: ", e);
            sendErrorResponse(e.getErrorCode() + " " + e.getMessage() + "\r\n");
        } finally {
            closeClientChannel();
        }
    }

    private void closeClientChannel() {
        try {
            if (clientChannel.isOpen()) {
                clientChannel.close();
                LOGGER.info("Closed client connection.");
            }
        } catch (IOException e) {
            LOGGER.error("Error while closing client channel: ", e);
        }
    }

    // Utility method to send error responses to the client
    private void sendErrorResponse(String message) {
        try {
            ByteBuffer b=ByteBuffer.allocate(100);
            b.clear();
            b.put(message.getBytes());
            b.flip();
            clientChannel.write(b);
        } catch (IOException e) {
            LOGGER.error("Failed to send error response to client: ", e);
        }
    }

}
