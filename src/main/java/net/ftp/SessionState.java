package net.ftp;

import java.nio.ByteBuffer;

public class SessionState {

    private String currentDirectory;
    private final ByteBuffer buffer;
    boolean flag=true;

    public SessionState() {
        this.currentDirectory = "/home/asvanth/IdeaProjects/my-java-assignments/server_files";
        this.buffer = ByteBuffer.allocate(1024); // Allocate once for the session
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
