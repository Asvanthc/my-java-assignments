package net.ftp;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;

import net.ftp.exceptions.InvalidCommandException;
import net.ftp.handler.Command;
import net.ftp.handler.QuitCommandHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Iterator;

public class FtpNioServer {
    private static final int PORT = 2121;
    private static final Logger LOGGER = LogManager.getLogger(FtpNioServer.class);
    protected static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        try (Selector selector = Selector.open(); ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            serverSocketChannel.bind(new InetSocketAddress(PORT));
            serverSocketChannel.configureBlocking(false);  // Non-blocking mode
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            LOGGER.info("NIO FTP Server is running on port " + PORT);

            //noinspection InfiniteLoopStatement
            while (true) {
                if (selector.select() > 0) {
                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if (key.isAcceptable()) {
                            handleAccept(serverSocketChannel, selector);
                        } else if (key.isReadable()) {
                            handleRead(key);
                        }
                        keyIterator.remove();
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("IOE in NIO server from handleAccept():", e);
        }
    }

    private static void handleAccept(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ, new SessionState());  // Attach SessionState to each client

        LOGGER.info("Client connected via NIO");
    }

    private static void handleRead(SelectionKey key) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        SessionState sessionState = (SessionState) key.attachment();  // Retrieve SessionState for this client

        ByteBuffer buffer = sessionState.getBuffer();

        try {
            sessionState.resetBuffer();  // Reset buffer before reading new data
            int bytesRead = clientChannel.read(buffer);

            if (bytesRead == -1) {
                LOGGER.info("No data read. The client may be temporarily unavailable.");
                return;
            }

            // Only process if we have data to process
            if (bytesRead > 0) {
                buffer.flip();  // Prepare the buffer for reading
                byte[] data = new byte[buffer.limit()];
                buffer.get(data);
                String receivedData = new String(data);

                // Process command using existing CommandParser and send the response
                CommandParser parser = CommandParser.getInstance();
                Command cmd = parser.readAndParseCommand(new ByteBufferBackedInputStream(buffer), clientChannel.socket().getOutputStream(), sessionState);

                // Handle specific commands like QUIT to decide whether to close the connection
                if (cmd != null) {
                    String response = cmd.handle();
                    clientChannel.write(ByteBuffer.wrap(response.getBytes()));

                    // If command is QUIT, close the connection
                    if (cmd instanceof QuitCommandHandler) {
                        closeClientSocket(clientChannel);
                    }
                }

                // Reset the buffer for the next read
                sessionState.resetBuffer();
            }
        } catch (IOException e) {
            LOGGER.error("IOE during NIO read:", e);
            closeClientSocket(clientChannel);  // Ensure connection closure on error
        } catch (InvalidCommandException e) {
            sendErrorResponse(clientChannel, e.getMessage());
        }
    }


    private static void sendErrorResponse(SocketChannel clientChannel, String message) {
        try {
            message += "\n";
            clientChannel.write(ByteBuffer.wrap(message.getBytes()));
        } catch (IOException e) {
            LOGGER.error("Failed to send error response to client: ", e);
        }
    }

    private static void closeClientSocket(SocketChannel clientChannel) {
        try {
            if (clientChannel.isConnected()) {
                clientChannel.close();
                LOGGER.info("Closed client connection.");
            }
        } catch (IOException e) {
            LOGGER.error("Error while closing client socket: ", e);
        }
    }
}
