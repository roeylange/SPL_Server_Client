package bgu.spl.net.api.Messages;

import bgu.spl.net.api.MessageImpl;

public class Login extends MessageImpl {

    String userName;
    String password;
    String captcha;

    public Login(String userName, String password, String captcha) {
        super ((short) 2);
        this.password = password;
        this.userName = userName;
        this.captcha = captcha;
    }
    public String getUserName() { return userName; }
    public String getCaptcha() { return captcha; }
    public String getPassword() { return password; }
}

