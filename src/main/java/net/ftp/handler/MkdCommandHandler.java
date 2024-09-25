package net.ftp.handler;

import java.io.File;

public class MkdCommandHandler implements Command {

    private final String directoryPath;

    // Constructor to accept the directory path as an argument
    public MkdCommandHandler(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public String handle() {
        if (directoryPath == null || directoryPath.trim().isEmpty()) {
            return "500 Bad sequence of commands.\r\n";
        }

        File dir = new File(directoryPath);
        if (dir.mkdirs()) {
            return "257 Directory created.\r\n";  // Changed to 257 for successful directory creation
        } else {
            return "550 Directory creation failed.\r\n";  // Consistent error code
        }
    }
}
