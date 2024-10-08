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
                        sessionState.setFlagRead(true);
                        ClientNio.handleClientRequest(clientChannel, sessionState);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("IO Exception in main: ", e);
        }
    }
}
