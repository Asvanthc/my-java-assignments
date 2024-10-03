package net.ftp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

import net.ftp.exceptions.InvalidCommandException;
import net.ftp.handler.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FTPNioServer {
    private static final int PORT = 2121;
    private static final Logger LOGGER = LogManager.getLogger(FTPNioServer.class);

    public static void main(String[] args) {
        try (Selector selector = Selector.open(); ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            serverChannel.configureBlocking(false);
            serverChannel.bind(new InetSocketAddress(PORT));
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            LOGGER.info("Server is running on port " + PORT);

            while (true) {
                selector.select();
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isAcceptable()) {
                        SocketChannel clientChannel = serverChannel.accept();
                        clientChannel.configureBlocking(false);
                        SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
                        clientKey.attach(new SessionState()); // Attach a new session state to the selection key
                        LOGGER.info("Client connected");
                    } else if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        SessionState sessionState = (SessionState) key.attachment();
                        sessionState.flag=true;
                        handleClientRequest(clientChannel, sessionState);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("IO Exception in main: ", e);
        }
    }


    private static void handleClientRequest(SocketChannel clientChannel, SessionState sessionState) {
        if (sessionState == null) {
            LOGGER.error("SessionState not found for client. This should not happen.");
            closeClientChannel(clientChannel);
            return;
        }

        ByteBuffer buffer = sessionState.getBuffer();

        try {

            while (buffer.hasRemaining() && sessionState.flag) {
                try {
                    // Parse command from buffer
                    Command cmd = CommandParser.getInstance().readAndParseCommand(buffer, clientChannel, sessionState);

                    if (cmd instanceof QuitCommandHandler || cmd==null) {
                        LOGGER.info("Client requested connection termination.");
                        closeClientChannel(clientChannel);
                        return;
                    }
                    // Handle the command
                    String response = cmd.handle();
                    ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
                    clientChannel.write(responseBuffer);
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
