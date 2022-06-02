package bgu.spl.net.api.Messages;

import bgu.spl.net.api.MessageImpl;
import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class Notification extends MessageImpl {

    private char type;
    private String user;
    private String content;

    public Notification(char type, String user, String content) {
        super((short) 9);
        this.type = type;
        this.user = user;
        this.content = content;
    }
    public char getType() {
        return type;
    }
    public String getUser() {
        return user;
    }
    public String getContent() {
        return content;
    }
}
