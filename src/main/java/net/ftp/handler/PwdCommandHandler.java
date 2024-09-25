package net.ftp.handler;

import java.nio.file.Paths;
import net.ftp.SessionState;

public class PwdCommandHandler implements Command {

    public PwdCommandHandler() {
    }

    @Override
    public String handle() {
        return "222 \"" + SessionState.getInstance().getCurrentDirectory() + "\" is the current directory.\r\n";
    }
}
