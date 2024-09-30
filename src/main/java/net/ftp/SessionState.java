package net.ftp;

import java.nio.ByteBuffer;

public class SessionState {

    private String currentDirectory;
    private final ByteBuffer buffer;
    private final ByteBufferBackedInputStream byteBufferInputStream;

    public SessionState() {
        this.currentDirectory = "/home/asvanth/IdeaProjects/my-java-assignments/server_files";
        this.buffer = ByteBuffer.allocate(1024); // Allocate once
        this.byteBufferInputStream = new ByteBufferBackedInputStream(buffer);  // Create once
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

    // Reuse the same ByteBufferBackedInputStream instance
    public ByteBufferBackedInputStream getByteBufferInputStream() {
        return byteBufferInputStream;
    }

}

