package net.sock;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class ThreadPoolEchoServerTest {

    private ThreadPoolEchoServer server;

    @BeforeEach
    public void setup() {
        server = new ThreadPoolEchoServer();
    }

    // Test the communication with the client
    @Test
    public void testCommunicateWithClient() throws IOException {
        BufferedReader mockIn = mock(BufferedReader.class);
        PrintWriter mockOut = mock(PrintWriter.class);

        when(mockIn.readLine()).thenReturn("Hello", (String) null);  // Simulating client sending "Hello"

        ThreadPoolEchoServer.ClientHandler clientHandler = new ThreadPoolEchoServer.ClientHandler(mock(Socket.class));
        clientHandler.communicateWithClient(mockIn, mockOut);

        verify(mockOut, times(1)).println("Echo: Hello");
    }

    // Test the shutdownThreadPool method
    @Test
    public void testShutdownThreadPool() {
        ExecutorService mockThreadPool = mock(ExecutorService.class);
        when(mockThreadPool.isShutdown()).thenReturn(false);

        server.shutdownThreadPool(mockThreadPool);

        verify(mockThreadPool, times(1)).shutdown();
    }

    // Test accepting clients (mocking server socket and client socket)
    @Test
    public void testAcceptClients() throws IOException {
        ServerSocket mockServerSocket = mock(ServerSocket.class);
        Socket mockClientSocket = mock(Socket.class);
        ExecutorService mockThreadPool = mock(ExecutorService.class);

        when(mockServerSocket.accept()).thenReturn(mockClientSocket);
//        doNothing().when(mockThreadPool).submit(any(Runnable.class));

        server.acceptClients(mockServerSocket, mockThreadPool);

        verify(mockServerSocket, times(1)).accept();
        verify(mockThreadPool, times(1)).submit(any(Runnable.class));
    }

    // Test the closeClientSocket method
    @Test
    public void testCloseClientSocket() throws IOException {
        Socket mockSocket = mock(Socket.class);
        ThreadPoolEchoServer.ClientHandler clientHandler = new ThreadPoolEchoServer.ClientHandler(mockSocket);

        clientHandler.closeClientSocket();
        verify(mockSocket, times(1)).close();
    }
}
