package net.ftp.handler;

public class PassCommandHandler implements Command {

    private final String argument;

    // Constructor to accept the command argument (password or related data)
    public PassCommandHandler(String argument) {
        this.argument=argument;
    }

    @Override
    public String handle() {
        // Implement any logic for handling PASS command (like verifying user password)
        // Currently returns a success message for simplicity
        return "230 User " +argument+ " logged in, proceed.\r\n";
    }
}
