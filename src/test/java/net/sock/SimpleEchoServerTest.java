package net.sock;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import static org.junit.jupiter.api.Assertions.*;

public class SimpleEchoServerTest {

    private SimpleEchoServer server;

    @BeforeEach
    public void setup() {
        server = new SimpleEchoServer();
    }

    // Test the processMessage method
    @Test
    public void testProcessMessage() {
        String input = "Hello";
        String expectedOutput = "Echo: Hello";
        String actualOutput = server.processMessage(input);
        assertEquals(expectedOutput, actualOutput, "The processMessage method did not return the expected echo");
    }

    // Test the sendMessage method
    @Test
    public void testSendMessage() {
        PrintWriter mockWriter = mock(PrintWriter.class);
        server.sendMessage(mockWriter, "Echo: Hello");
        verify(mockWriter, times(1)).println("Echo: Hello");
    }

    // Test the receiveMessage method
    @Test
    public void testReceiveMessage() throws IOException {
        BufferedReader mockReader = mock(BufferedReader.class);
        when(mockReader.readLine()).thenReturn("Hello");
        String message = server.receiveMessage(mockReader);
        assertEquals("Hello", message, "The received message did not match the expected message");
    }

    // Test the handleClient method by simulating client communication
    @Test
    public void testHandleClient() throws IOException {
        Socket mockSocket = mock(Socket.class);
        BufferedReader mockIn = mock(BufferedReader.class);
        PrintWriter mockOut = mock(PrintWriter.class);

        when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream("Hello\n".getBytes()));
        when(mockSocket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(mockIn.readLine()).thenReturn("Hello", (String) null);  // Simulating client sending "Hello"

        SimpleEchoServer spyServer = spy(server);
        doNothing().when(spyServer).communicateWithClient(any(), any());

        spyServer.handleClient(mockSocket);

        verify(spyServer, times(1)).communicateWithClient(any(), any());
    }

    // Test the communicateWithClient method
    @Test
    public void testCommunicateWithClient() throws IOException {
        BufferedReader mockIn = mock(BufferedReader.class);
        PrintWriter mockOut = mock(PrintWriter.class);

        when(mockIn.readLine()).thenReturn("Hello", (String) null);

        server.communicateWithClient(mockIn, mockOut);

        verify(mockOut, times(1)).println("Echo: Hello");
        verify(mockIn, times(2)).readLine();  // Once for the message, and once for null (indicating end of input)
    }

    // Test the closeClientSocket method
    @Test
    public void testCloseClientSocket() throws IOException {
        Socket mockSocket = mock(Socket.class);
        server.closeClientSocket(mockSocket);
        verify(mockSocket, times(1)).close();
    }
}
