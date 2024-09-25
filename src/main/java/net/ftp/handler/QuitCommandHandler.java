package net.ftp.handler;
import java.nio.file.Paths;
public class QuitCommandHandler implements Command {

    public QuitCommandHandler() {
    }

    @Override
    public String handle() {
        return "999 Goodbye.\r\n";
    }
}
