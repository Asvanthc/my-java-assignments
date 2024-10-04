package net.ftp;

import net.ftp.exceptions.InvalidCommandException;
import net.ftp.handler.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommandParserTest {

    private CommandParser parser;
    private ByteBuffer buffer;
    private SocketChannel mockClientChannel;
    private SessionState mockSessionState;

    @BeforeEach
    public void setUp() {
        parser = CommandParser.getInstance();
        buffer = ByteBuffer.allocate(1024);
        mockClientChannel = mock(SocketChannel.class);
        mockSessionState = new SessionState();
    }

    @Test
    public void testValidPassCommand() throws Exception {
        String inputCommand = "PASS password123\n";
        parser.bytesRead=inputCommand.length();
        buffer.put(inputCommand.getBytes());
        buffer.flip();  // Prepare buffer for reading

        Command command = parser.readAndParseCommand(buffer, mockClientChannel, mockSessionState);
        assertTrue(command instanceof PassCommandHandler);
    }

    @Test
    public void testValidListCommand() throws Exception {
        String inputCommand = "LIST\n";
        parser.bytesRead=inputCommand.length();
        buffer.put(inputCommand.getBytes());
        buffer.flip();

        Command command = parser.readAndParseCommand(buffer, mockClientChannel, mockSessionState);
        assertInstanceOf(ListCommandHandler.class, command);
    }

    @Test
    public void testValidPwdCommand() throws Exception {
        String inputCommand = "PWD\n";
        parser.bytesRead=inputCommand.length();
        buffer.put(inputCommand.getBytes());
        buffer.flip();

        Command command = parser.readAndParseCommand(buffer, mockClientChannel, mockSessionState);
        assertInstanceOf(PwdCommandHandler.class, command);
    }

    @Test
    public void testValidCwdCommand() throws Exception {
        String inputCommand = "CWD /new/directory\n";
        parser.bytesRead=inputCommand.length();
        buffer.put(inputCommand.getBytes());
        buffer.flip();

        Command command = parser.readAndParseCommand(buffer, mockClientChannel, mockSessionState);
        assertInstanceOf(CwdCommandHandler.class, command);
    }

    @Test
    public void testValidMkdCommand() throws Exception {
        String inputCommand = "MKD /new/folder\n";
        parser.bytesRead=inputCommand.length();
        buffer.put(inputCommand.getBytes());
        buffer.flip();

        Command command = parser.readAndParseCommand(buffer, mockClientChannel, mockSessionState);
        assertInstanceOf(MkdCommandHandler.class, command);
    }

    @Test
    public void testValidRmdCommand() throws Exception {
        String inputCommand = "RMD /new/folder\n";
        parser.bytesRead=inputCommand.length();
        buffer.put(inputCommand.getBytes());
        buffer.flip();

        Command command = parser.readAndParseCommand(buffer, mockClientChannel, mockSessionState);
        assertInstanceOf(RmdCommandHandler.class, command);
    }

    @Test
    public void testValidStorCommand() throws Exception {
        String inputCommand = "STOR filename.txt 1000\n";
        parser.bytesRead=inputCommand.length();
        buffer.put(inputCommand.getBytes());
        buffer.flip();

        Command command = parser.readAndParseCommand(buffer, mockClientChannel, mockSessionState);
        assertInstanceOf(StorCommandHandler.class, command);
    }

    @Test
    public void testValidRetrCommand() throws Exception {
        String inputCommand = "RETR filename.txt\n";
        parser.bytesRead=inputCommand.length();
        buffer.put(inputCommand.getBytes());
        buffer.flip();

        Command command = parser.readAndParseCommand(buffer, mockClientChannel, mockSessionState);
        assertInstanceOf(RetrCommandHandler.class, command);
    }

    @Test
    public void testValidQuitCommand() throws Exception {
        String inputCommand = "QUIT\n";
        parser.bytesRead=inputCommand.length();
        buffer.put(inputCommand.getBytes());
        buffer.flip();

        Command command = parser.readAndParseCommand(buffer, mockClientChannel, mockSessionState);
        assertInstanceOf(QuitCommandHandler.class, command);
    }

    @Test
    public void testInvalidCommand() {
        String inputCommand = "INVALID_CMD\n";
        parser.bytesRead=inputCommand.length();
        buffer.put(inputCommand.getBytes());
        buffer.flip();

        assertThrows(InvalidCommandException.class, () -> parser.readAndParseCommand(buffer, mockClientChannel, mockSessionState));
    }


    @Test
    public void testMultiLinePipedCommands() throws Exception {
        String inputCommand = "PASS password123\nLIST\n";
        parser.bytesRead=inputCommand.length();
        buffer.put(inputCommand.getBytes());
        buffer.flip();

        // Parse first command (PASS)
        Command firstCommand = parser.readAndParseCommand(buffer, mockClientChannel, mockSessionState);
        assertInstanceOf(PassCommandHandler.class, firstCommand);

        // Parse second command (LIST)
        Command secondCommand = parser.readAndParseCommand(buffer, mockClientChannel, mockSessionState);
        assertInstanceOf(ListCommandHandler.class, secondCommand);
    }

    @Test
    public void testComplexMultiLinePipedCommands() throws Exception {
        String inputCommand = "LIST\nRETR sample3.txt\nPWD\nLIST\nSTOR sample2.txt 5\n12345PWD\nlist\nretr sample3.txt\n";
        parser.bytesRead=inputCommand.length();
        buffer.put(inputCommand.getBytes());
        buffer.flip();

        // Parse and verify the first command (LIST)
        Command firstCommand = parser.readAndParseCommand(buffer, mockClientChannel, mockSessionState);
        assertInstanceOf(ListCommandHandler.class, firstCommand);

        // Parse and verify the second command (RETR)
        Command secondCommand = parser.readAndParseCommand(buffer, mockClientChannel, mockSessionState);
        assertInstanceOf(RetrCommandHandler.class, secondCommand);
        assertEquals("sample3.txt", ((RetrCommandHandler) secondCommand).getFileName());

        // Parse and verify the third command (PWD)
        Command thirdCommand = parser.readAndParseCommand(buffer, mockClientChannel, mockSessionState);
        assertInstanceOf(PwdCommandHandler.class, thirdCommand);

        // Parse and verify the fourth command (LIST)
        Command fourthCommand = parser.readAndParseCommand(buffer, mockClientChannel, mockSessionState);
        assertInstanceOf(ListCommandHandler.class, fourthCommand);

        // Parse and verify the fifth command (STOR)
        Command fifthCommand = parser.readAndParseCommand(buffer, mockClientChannel, mockSessionState);
        assertInstanceOf(StorCommandHandler.class, fifthCommand);
        assertEquals("sample2.txt", ((StorCommandHandler) fifthCommand).getFileName());
        assertEquals(5, ((StorCommandHandler) fifthCommand).getSize());

        // Read the next data for STOR (content '12345')
        byte[] dataBuffer = new byte[5];
        buffer.get(dataBuffer);
        assertArrayEquals("12345".getBytes(), dataBuffer);

        // Parse and verify the next command (PWD)
        Command sixthCommand = parser.readAndParseCommand(buffer, mockClientChannel, mockSessionState);
        assertInstanceOf(PwdCommandHandler.class, sixthCommand);

        // Parse and verify the next command (LIST)
        Command seventhCommand = parser.readAndParseCommand(buffer, mockClientChannel, mockSessionState);
        assertInstanceOf(ListCommandHandler.class, seventhCommand);

        // Parse and verify the final command (RETR)
        Command eighthCommand = parser.readAndParseCommand(buffer, mockClientChannel, mockSessionState);
        assertInstanceOf(RetrCommandHandler.class, eighthCommand);
        assertEquals("sample3.txt", ((RetrCommandHandler) eighthCommand).getFileName());
    }
}
