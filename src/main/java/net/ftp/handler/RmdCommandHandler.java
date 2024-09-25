package net.ftp.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RmdCommandHandler implements Command {
    private static final Logger LOGGER = LogManager.getLogger(RmdCommandHandler.class);
    private final String directoryPath;

    // Constructor to accept the directory path as an argument
    public RmdCommandHandler(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public String handle() {
        if (directoryPath == null || directoryPath.trim().isEmpty()) {
            return "500 Bad sequence of commands.\r\n";
        }

        File dir = new File(directoryPath);

        try {
            // Delete the directory and its contents
            Files.walk(dir.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            return "250 Directory removed.\r\n";  // Changed to 250 for successful removal
        } catch (IOException e) {
            LOGGER.error("IOE during directory removal: ", e);
            return "550 Directory removal failed.\r\n";  // Consistent error code
        }
    }
}
