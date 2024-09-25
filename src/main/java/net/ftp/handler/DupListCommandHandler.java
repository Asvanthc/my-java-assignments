package net.ftp.handler;

import java.io.File;

public class DupListCommandHandler {
    public static String handle() {
        File currentDir = new File("server_fles");
        String[] fileList = currentDir.list();
        int fileCount = (fileList != null) ? fileList.length : 0;

        if (fileList == null || fileCount == 0) {
            return "404 No files in directory.\r\n";
        }

        StringBuilder response = new StringBuilder("150 directory exists listing files...\n");
        long totalSize = 0;

        for (String file : fileList) {
            File currentFile = new File(currentDir, file);
            if (currentFile.isFile()) {
                totalSize += currentFile.length();
            }
            response.append(file).append("\n");
        }

        response.append("Total files listed: ").append(fileCount).append("\n");
        response.append("Total size of Directory: ").append(totalSize).append(" bytes\n");
        response.append("200 Transfer complete.\r\n");
        return response.toString();
    }
}
