package net.ftp;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FtpServer {
    private static final int PORT = 2121;
    private static final Logger LOGGER = LogManager.getLogger(FtpServer.class);
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            LOGGER.info("Server is running on port " + PORT);
            //noinspection InfiniteLoopStatement
            while (true) {
                Socket clientSocket = serverSocket.accept();

                LOGGER.info("Client connected");
                new Thread(new ClientConnection(clientSocket)).start();
            }
        } catch (IOException e) {
            LOGGER.error("IOE at main:",e);
        }
    }
}
