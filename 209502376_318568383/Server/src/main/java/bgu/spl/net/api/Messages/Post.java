package bgu.spl.net.api.Messages;

import bgu.spl.net.api.MessageImpl;

import java.util.LinkedList;

public class Post extends MessageImpl {

    private LinkedList<String> taggedNames=new LinkedList<String>();
    private String content;

    public Post(String content) {
        super((short) 5);
        this.content = content;
    }

    public Post(String content,LinkedList<String> taggedNames) {
        super((short) 5);
        this.taggedNames = taggedNames;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LinkedList<String> getTaggedNames() { return ((LinkedList<String>)taggedNames); }

    public void setTaggedNames(LinkedList<String> taggedNames) { this.taggedNames = taggedNames; }

}
