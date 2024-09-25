package net.ftp.handler;

import net.ftp.SessionState;

import java.io.File;

public class ListCommandHandler implements Command {

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";

    private final File currentDir;

    // Constructor to accept the directory path or other initialization parameters
    public ListCommandHandler() {
        this.currentDir = new File(SessionState.getInstance().getCurrentDirectory()); // Default to the "server_files" directory
    }

    @Override
    public String handle() {
        String[] fileList = currentDir.list();
        int fileCount = (fileList != null) ? fileList.length : 0;

        if (fileList == null || fileCount == 0) {
            return RED + "404 No files in directory." + RESET + "\r\n";
        }

        StringBuilder response = new StringBuilder(YELLOW + "150 directory exists, listing files...\n");
        long totalSize = 0;
        response.append("Total files to be listed: ").append(fileCount).append(RESET).append("\n");

        int count = 0;
        for (String file : fileList) {
            File currentFile = new File(currentDir, file);
            if (currentFile.isFile()) {
                totalSize += currentFile.length();
            }
            ++count;
            response.append(count).append(") ").append(file).append("\n");
        }
        response.append(YELLOW + "Total size of Directory: ").append(totalSize).append(" bytes\n" + RESET);
        response.append(GREEN + "200 Transfer complete.\r\n\n" + RESET);
        return response.toString();
    }
}
