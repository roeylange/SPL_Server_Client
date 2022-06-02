package bgu.spl.net.api.Messages;

import bgu.spl.net.api.MessageImpl;
import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class Error_spl extends MessageImpl {

    private final short messageOpCope;

    public Error_spl(short messageOpCope) {
        super((short)11);
        this.messageOpCope = messageOpCope;
    }

    public short getMessageOpCope() {return messageOpCope;}
}
