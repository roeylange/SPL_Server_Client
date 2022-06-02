package bgu.spl.net.api.Messages;

import bgu.spl.net.api.MessageImpl;
import bgu.spl.net.srv.bidi.ConnectionHandler;

public class Register extends MessageImpl {

    private String userName,Password;
    private String bDay;
    private ConnectionHandler handler;


    public Register(String userName, String password, String bday) {
        super ((short) 1);
        this.userName = userName;
        Password = password;
        this.bDay = bday;
    }

    public String getUserName() { return userName; }

    public String getPassword() { return Password; }

    public String getbDay() {return bDay;}

    public ConnectionHandler getHandler() { return handler; }

    public void setHandler(ConnectionHandler handler) { this.handler = handler; }

}
