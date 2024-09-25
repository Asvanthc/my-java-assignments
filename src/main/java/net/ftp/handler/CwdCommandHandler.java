package net.ftp.handler;

import net.ftp.SessionState;
import java.io.File;

public class CwdCommandHandler implements Command {

    private final String directoryPath;

    // Constructor to accept the directory path as an argument
    public CwdCommandHandler(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public String handle() {
        if (directoryPath == null || directoryPath.trim().isEmpty()) {
            return "500 Bad sequence of commands.\r\n";
        }

        File dir = new File(directoryPath);
        if (dir.exists() && dir.isDirectory()) {
            SessionState.getInstance().setCurrentDirectory(dir.getAbsolutePath()); // Update session directory
            return "250 Directory changed to " + dir.getAbsolutePath() + ".\r\n";
        } else {
            return "404 No such directory.\r\n";
        }
    }
}
