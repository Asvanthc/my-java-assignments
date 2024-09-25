package net.ftp;
import java.io.*;
import java.net.Socket;
import net.ftp.handler.*;
import net.ftp.exceptions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientConnection implements Runnable {
    private final Socket clientSocket;
    private static final Logger LOGGER = LogManager.getLogger(ClientConnection.class);

    public ClientConnection(Socket clientSocket) {
        this.clientSocket = clientSocket;
//        SessionState sessionState = new SessionState(); // Each client gets its own SessionState
    }

    @Override
    public void run() {
        try (BufferedInputStream in = new BufferedInputStream(clientSocket.getInputStream());
             OutputStream out = clientSocket.getOutputStream()) {

            boolean isConnectionActive = true;

            while (isConnectionActive) {
                try {
                    // Read and parse command
                    CommandParser cp = new CommandParser();
                    Command cmd = cp.readAndParseCommand(in, out);
//                    Command cmd = CommandParser.readAndParseCommand(in, out);
                    if (cmd == null) {
                        LOGGER.info("Client closed connection or sent invalid command.");
                        break;
                    }

                    // Handle command and send the response
                    String response = cmd.handle();
                    out.write(response.getBytes());
                    out.flush();

                    // Check if the response indicates termination (e.g., QUIT)
                    if (response.startsWith("999")) {
                        LOGGER.info("Client requested connection termination.");
                        isConnectionActive = false;
                    }
                } catch (FtpCommandException e) {  // Catch any FTP-related exception
                    LOGGER.warn("Error processing command: {}", e.getMessage());
                    sendErrorResponse(out, e.getErrorCode() + " " + e.getMessage() + "\r\n");
                }
            }
        } catch (IOException e) {
            LOGGER.error("IO Exception in client connection: ", e);
        } finally {
            closeClientSocket();
        }
    }

    // Utility method to send error responses to the client
    private void sendErrorResponse(OutputStream out, String message) {
        try {
            out.write(message.getBytes());
            out.flush();
        } catch (IOException e) {
            LOGGER.error("Failed to send error response to client: ", e);
        }
    }

    // Utility method to close the client socket
    private void closeClientSocket() {
        try {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
                LOGGER.info("Closed client connection.");
            }
        } catch (IOException e) {
            LOGGER.error("Error while closing client socket: ", e);
        }
    }

}
