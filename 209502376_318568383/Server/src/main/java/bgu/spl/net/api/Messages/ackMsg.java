package bgu.spl.net.api.Messages;

import bgu.spl.net.api.MessageImpl;
import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;
import java.util.LinkedList;

public class ackMsg extends MessageImpl {

    private final short messageOpCope;
    private short age, numOfPosts, numOfFollowers, numOfFollowing;
    private String username;
    private int followORunfollow = 0;

    //used for REGISTER,LOGIN,LOGOUT,POST,PM
    public ackMsg(short messageOpCope) {

        super((short) 10);
        this.messageOpCope = messageOpCope;
    }

    //used for FOLLOW,UNFOLLOW,BLOCK
    public ackMsg(short messageOpCope, String userName) {
        super((short) 10);
        this.messageOpCope = messageOpCope;
        this.username = userName;
    }

    //used for STAT, LOGSTAT
    public ackMsg(short messageOpCope,short age, short numOfPosts, short numOfFollowers, short numOfFollowing) {
        super((short) 10);
        this.age = age;
        this.messageOpCope = messageOpCope;
        this.numOfPosts = numOfPosts;
        this.numOfFollowers = numOfFollowers;
        this.numOfFollowing = numOfFollowing;
    }

    public int getFollowORunfollow() { return followORunfollow; }

    public void setFollowORunfollow(int followORunfollow) { this.followORunfollow = followORunfollow; }

    public short getMessageOpCope() {return messageOpCope;}

    public short getAge() {return age;}

    public String getUsername() {return username;}

    public short getNumOfPosts() {return numOfPosts;}

    public short getNumOfFollowers() {return numOfFollowers;}

    public short getNumOfFollowing() {return numOfFollowing;}

}
