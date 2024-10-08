package net.ftp;

import net.ftp.handler.StorCommandHandler;

import java.nio.ByteBuffer;

public class SessionState {

    private String currentDirectory;
    private final ByteBuffer buffer;
    private boolean flagRead=true;
    private boolean isWritingFile = false;
    protected int bytesRead=0;
    StringBuilder commandLine = new StringBuilder();;
    StorCommandHandler sch;

    public SessionState() {
        this.currentDirectory = "/home/asvanth/IdeaProjects/my-java-assignments/server_files";
        this.buffer = ByteBuffer.allocate(1024); // Allocate once for the session
    }

    public void setStore(StorCommandHandler sch){
        this.sch=sch;
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

    public boolean isBufferReadyForReading() {
        return buffer.position() == 0 && buffer.limit() == buffer.capacity();
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
