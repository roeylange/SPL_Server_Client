package bgu.spl.net.api.Messages;

import bgu.spl.net.api.MessageImpl;

import java.io.Serializable;

public class Block extends MessageImpl {

    private String blockedUser;

    public Block(String blockedUser) {
        super((short) 12);
        this.blockedUser = blockedUser;
    }


    public String getBlockedUser() {return blockedUser;}
}
