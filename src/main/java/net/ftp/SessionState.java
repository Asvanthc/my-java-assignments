package net.ftp;

import java.nio.ByteBuffer;

public class SessionState {

    private String currentDirectory;
    private final ByteBuffer buffer;
    private boolean flagRead=true;
    private boolean isWritingFile = false;

    public SessionState() {
        this.currentDirectory = "/home/asvanth/IdeaProjects/my-java-assignments/server_files";
        this.buffer = ByteBuffer.allocate(1024); // Allocate once for the session
    }

    public boolean flagRead() {
        return flagRead;
    }

    public void setFlagRead(boolean flagRead) {
        this.flagRead = flagRead;
    }

    public boolean isWritingFile() {
        return isWritingFile;
    }

    public void setWritingFile(boolean writingFile) {
        isWritingFile = writingFile;
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public void resetBuffer() {
        buffer.clear();  // Reuse the buffer
    }
}
