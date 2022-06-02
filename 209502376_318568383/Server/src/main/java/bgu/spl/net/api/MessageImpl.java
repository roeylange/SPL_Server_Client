package bgu.spl.net.api;

public abstract class MessageImpl {

    private final short opCode;

    protected MessageImpl(short opCode) {this.opCode = opCode;}

    public Short getOpCode() {return opCode;}
}
