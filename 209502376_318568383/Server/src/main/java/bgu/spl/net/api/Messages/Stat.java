package bgu.spl.net.api.Messages;

import bgu.spl.net.api.MessageImpl;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

public class Stat extends MessageImpl {

    private LinkedList<String> taggedUsers;

    public Stat(LinkedList<String> taggedUsers) {
        super((short) 8);
        this.taggedUsers = taggedUsers;
    }

    public LinkedList<String> getTaggedUsers() {return taggedUsers;}
}
