package bgu.spl.net.srv;

import bgu.spl.net.api.MessageImpl;
import bgu.spl.net.api.Messages.Pending;

import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {
    private int id=-1;
    private String username, password;
    private String bDay;
    private LinkedList<String> followers, following, blocked;
    private LinkedBlockingQueue<Pending> pendingMsgs;
    private boolean isOnline=false;
    private int numOfPosts;

    public Client(){}

    public Client(String username, String password, String bDay) {
        this.username = username;
        this.password = password;
        this.bDay = bDay;
        followers = new LinkedList<>();
        following = new LinkedList<>();
        blocked = new LinkedList<>();
        pendingMsgs = new LinkedBlockingQueue<>();
        this.numOfPosts=0;
    }

    public int getNumOfPosts() {return numOfPosts;}

    public String getbDay() {return bDay;}

    public void incrementNumOfPosts() { this.numOfPosts+=1;}

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LinkedList<String> getFollowers() {return followers;}

    public LinkedList<String> getFollowing() {
        return following;
    }

    public LinkedList<String> getBlocked() {
        return blocked;
    }

    public LinkedBlockingQueue<Pending> getPendingMsgs() {
        return pendingMsgs;
    }

    public void addFollower(String f) {
        this.followers.add(f);
    }

    public void addFollowing(String f) {
        this.following.add(f);
    }

    public void addBlocked(String f) {
        this.blocked.add(f);
    }

    public void addMsg(Pending m) {
        this.pendingMsgs.add(m);
    }

    public void removeFollowing(String f) {
        this.following.remove(f);
    }

    public void removeFollower(String f) {
        this.followers.remove(f);
    }

    public void removeBlocked(String f) {
        this.blocked.remove(f);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}