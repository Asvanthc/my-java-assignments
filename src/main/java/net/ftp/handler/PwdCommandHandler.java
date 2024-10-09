package net.ftp.handler;

import net.ftp.SessionState;

public class PwdCommandHandler implements Command {
    SessionState sessionState;
    public PwdCommandHandler(SessionState sessionState) {
        this.sessionState=sessionState;
    }

    @Override
    public String handle() {
        return "222 \"" + sessionState.getCurrentDirectory() + "\" is the current directory.\r\n";
    }
}
