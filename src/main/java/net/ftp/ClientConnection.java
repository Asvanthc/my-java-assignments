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

    public ClientConnection(SocketChannel clientChannel) {
        this.clientChannel = clientChannel;
        this.sessionState = new SessionState();
    }

    @Override
    public void run() {
        ByteBuffer buffer = sessionState.getBuffer();
        ByteBuffer buffer1= ByteBuffer.allocate(1024);
        boolean isConnectionActive = true;

        try {
            while (isConnectionActive) {
                Command cmd = CommandParser.getInstance().readAndParseCommand(buffer, clientChannel, sessionState);
                if (cmd == null) {
                    LOGGER.info("Invalid command received or client disconnected.");
                    break;
                }

                String response = cmd.handle();
                buffer1.clear();
                buffer1.put(response.getBytes());
                buffer1.flip();
                 clientChannel.write(buffer1);

                if (response.startsWith("999")) {
                    LOGGER.info("Client requested connection termination.");
                    isConnectionActive = false;
                }
            }
        } catch (IOException e) {
            LOGGER.error("IO Exception in client connection: ", e);
        } catch (InvalidCommandException e) {
            LOGGER.warn("Invalid cmd client connection: ", e);
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
}
