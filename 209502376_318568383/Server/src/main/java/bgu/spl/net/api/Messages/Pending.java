package bgu.spl.net.api.Messages;

public class Pending {
    private String sender,content,receiver;
    private char type;


    public Pending(String sender, String content, String receiver, char type) {
        this.sender = sender;
        this.content = content;
        this.receiver = receiver;
        this.type = type;
    }


    //post
    public Pending(String sender, String content, char type) {
        this.sender = sender;
        this.content = content;
        this.type = type;
    }

    public String getSender() { return sender; }

    public String getContent() { return content; }

    public String getReceiver() { return receiver; }

    public char getType() { return type; }
}
