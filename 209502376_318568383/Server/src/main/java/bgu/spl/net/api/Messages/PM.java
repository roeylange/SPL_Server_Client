package bgu.spl.net.api.Messages;

import bgu.spl.net.api.MessageImpl;

public class PM extends MessageImpl {

    private String dstUserName, content;
    private String date;

    public PM(String dstUserName, String content, String date) {
        super((short) 6);
        this.dstUserName = dstUserName;
        this.content = content;
        this.date  = date;
    }

    public String getDate() {return date;}

    public String getDstUserName() {return dstUserName;}

    public String getContent() {return content;}

    public void setContent(String content) {this.content = content;}

}
