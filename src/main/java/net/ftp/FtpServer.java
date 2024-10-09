package net.ftp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FtpServer {
    private static final int PORT = 2121;
    private static final Logger LOGGER = LogManager.getLogger(FtpServer.class);

    public static void main(String[] args) {
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            serverChannel.bind(new InetSocketAddress(PORT));
            LOGGER.info("Server is running on port " + PORT);

            //noinspection InfiniteLoopStatement
            while (true) {
                SocketChannel clientChannel = serverChannel.accept();
                LOGGER.info("Client connected");
                new Thread(new ClientConnection(clientChannel)).start();
            }
        } catch (IOException e) {
            LOGGER.error("IOE at main:", e);
        }
    }
}
