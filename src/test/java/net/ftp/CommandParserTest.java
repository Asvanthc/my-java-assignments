package net.ftp;

import net.ftp.exceptions.CommandTooLongException;
import net.ftp.exceptions.InvalidCommandException;
import net.ftp.handler.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;

public class CommandParserTest {

    private CommandParser parser;
    private OutputStream outputStream;

    @BeforeEach
    public void setUp() {
        parser = CommandParser.getInstance();
        outputStream = new ByteArrayOutputStream();
    }

    @Test
    public void testValidPassCommand() throws Exception {
        String inputCommand = "PASS password123\n";
        InputStream inputStream = new ByteArrayInputStream(inputCommand.getBytes());

        Command command = parser.readAndParseCommand(inputStream, outputStream, new SessionState());
        assertTrue(command instanceof PassCommandHandler);
    }

    @Test
    public void testValidListCommand() throws Exception {
        String inputCommand = "LIST\n";
        InputStream inputStream = new ByteArrayInputStream(inputCommand.getBytes());

        Command command = parser.readAndParseCommand(inputStream, outputStream, new SessionState());
        assertInstanceOf(ListCommandHandler.class, command);
    }

    @Test
    public void testValidPwdCommand() throws Exception {
        String inputCommand = "PWD\n";
        InputStream inputStream = new ByteArrayInputStream(inputCommand.getBytes());

        Command command = parser.readAndParseCommand(inputStream, outputStream, new SessionState());
        assertInstanceOf(PwdCommandHandler.class, command);
    }

    @Test
    public void testValidCwdCommand() throws Exception {
        String inputCommand = "CWD /new/directory\n";
        InputStream inputStream = new ByteArrayInputStream(inputCommand.getBytes());

        Command command = parser.readAndParseCommand(inputStream, outputStream, new SessionState());
        assertInstanceOf(CwdCommandHandler.class, command);
    }

    @Test
    public void testValidMkdCommand() throws Exception {
        String inputCommand = "MKD /new/folder\n";
        InputStream inputStream = new ByteArrayInputStream(inputCommand.getBytes());

        Command command = parser.readAndParseCommand(inputStream, outputStream, new SessionState());
        assertInstanceOf(MkdCommandHandler.class, command);
    }

    @Test
    public void testValidRmdCommand() throws Exception {
        String inputCommand = "RMD /new/folder\n";
        InputStream inputStream = new ByteArrayInputStream(inputCommand.getBytes());

        Command command = parser.readAndParseCommand(inputStream, outputStream, new SessionState());
        assertInstanceOf(RmdCommandHandler.class, command);
    }

    @Test
    public void testValidStorCommand() throws Exception {
        String inputCommand = "STOR filename.txt 1000\n";
        InputStream inputStream = new ByteArrayInputStream(inputCommand.getBytes());

        Command command = parser.readAndParseCommand(inputStream, outputStream, new SessionState());
        assertInstanceOf(StorCommandHandler.class, command);
    }

    @Test
    public void testValidRetrCommand() throws Exception {
        String inputCommand = "RETR filename.txt\n";
        InputStream inputStream = new ByteArrayInputStream(inputCommand.getBytes());

        Command command = parser.readAndParseCommand(inputStream, outputStream, new SessionState());
        assertInstanceOf(RetrCommandHandler.class, command);
    }

    @Test
    public void testValidQuitCommand() throws Exception {
        String inputCommand = "QUIT\n";
        InputStream inputStream = new ByteArrayInputStream(inputCommand.getBytes());

        Command command = parser.readAndParseCommand(inputStream, outputStream, new SessionState());
        assertInstanceOf(QuitCommandHandler.class, command);
    }

    @Test
    public void testInvalidCommand() {
        String inputCommand = "INVALID_CMD\n";
        InputStream inputStream = new ByteArrayInputStream(inputCommand.getBytes());

        assertThrows(InvalidCommandException.class, () -> parser.readAndParseCommand(inputStream, outputStream, new SessionState()));
    }

    @Test
    public void testCommandTooLongException() {
        // Command exceeds the buffer/mark limit of 1024 characters
        String inputCommand = "A".repeat(1025) + "\n";
        InputStream inputStream = new ByteArrayInputStream(inputCommand.getBytes());

        assertThrows(InvalidCommandException.class, () -> parser.readAndParseCommand(inputStream, outputStream, new SessionState()));
    }

    @Test
    public void testMultiLinePipedCommands() throws Exception {
        String inputCommand = "PASS password123\nLIST\n";
        InputStream inputStream = new ByteArrayInputStream(inputCommand.getBytes());

        // Parse first command (PASS)
        Command firstCommand = parser.readAndParseCommand(inputStream, outputStream, new SessionState());
        assertInstanceOf(PassCommandHandler.class, firstCommand);

        // Parse second command (LIST)
        Command secondCommand = parser.readAndParseCommand(inputStream, outputStream, new SessionState());
        assertInstanceOf(ListCommandHandler.class, secondCommand);
    }

    @Test
    public void testComplexMultiLinePipedCommands() throws Exception {
        String inputCommand = "LIST\nRETR sample3.txt\nPWD\nLIST\nSTOR sample2.txt 5\n12345PWD\nLIST\nRETR sample2.txt\n";
        InputStream inputStream = new ByteArrayInputStream(inputCommand.getBytes());

        // Parse and verify the first command (LIST)
        Command firstCommand = parser.readAndParseCommand(inputStream, outputStream, new SessionState());
        assertInstanceOf(ListCommandHandler.class, firstCommand);

        // Parse and verify the second command (RETR)
        Command secondCommand = parser.readAndParseCommand(inputStream, outputStream, new SessionState());
        assertInstanceOf(RetrCommandHandler.class, secondCommand);
        assertEquals("sample3.txt", ((RetrCommandHandler) secondCommand).getFileName());

        // Parse and verify the third command (PWD)
        Command thirdCommand = parser.readAndParseCommand(inputStream, outputStream, new SessionState());
        assertInstanceOf(PwdCommandHandler.class, thirdCommand);

        // Parse and verify the fourth command (LIST)
        Command fourthCommand = parser.readAndParseCommand(inputStream, outputStream, new SessionState());
        assertInstanceOf(ListCommandHandler.class, fourthCommand);

        // Parse and verify the fifth command (STOR)
        Command fifthCommand = parser.readAndParseCommand(inputStream, outputStream, new SessionState());
        assertInstanceOf(StorCommandHandler.class, fifthCommand);
        assertEquals("sample2.txt", ((StorCommandHandler) fifthCommand).getFileName());
        assertEquals(5, ((StorCommandHandler) fifthCommand).getSize());

        // Parse and verify the next data for STOR (content '12345')
        byte[] buffer = new byte[5];
        inputStream.read(buffer);
        assertArrayEquals("12345".getBytes(), buffer);

        // Parse and verify the next command (PWD)
        Command sixthCommand = parser.readAndParseCommand(inputStream, outputStream, new SessionState());
        assertInstanceOf(PwdCommandHandler.class, sixthCommand);

        // Parse and verify the next command (LIST)
        Command seventhCommand = parser.readAndParseCommand(inputStream, outputStream, new SessionState());
        assertInstanceOf(ListCommandHandler.class, seventhCommand);

        // Parse and verify the final command (RETR)
        Command eighthCommand = parser.readAndParseCommand(inputStream, outputStream, new SessionState());
        assertInstanceOf(RetrCommandHandler.class, eighthCommand);
        assertEquals("sample2.txt", ((RetrCommandHandler) eighthCommand).getFileName());
    }

}
