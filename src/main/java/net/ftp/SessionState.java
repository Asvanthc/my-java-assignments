package net.ftp;

public class SessionState {

    private static final SessionState INSTANCE = new SessionState();

    private String currentDirectory;

    private SessionState() {
        // Set a default directory if needed
        this.currentDirectory = "/home/asvanth/IdeaProjects/my-java-assignments/server_files";
    }

    public static SessionState getInstance() {
        return INSTANCE;
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }
}
