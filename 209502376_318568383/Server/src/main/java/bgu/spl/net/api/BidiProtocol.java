package bgu.spl.net.api;

import bgu.spl.net.api.Messages.*;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.srv.Client;
import java.time.LocalDate;

import java.time.Period;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BidiProtocol implements BidiMessagingProtocol {

    private DataManager dt= DataManager.getInstance();
    private ConnectionsImpl con = ConnectionsImpl.getInstance();
    private boolean shouldTerminate=false;
    private int id;
    private LinkedList<String> filter = new LinkedList<String>();
    public BidiProtocol() {
        filter.add("war");
        filter.add("trump");
        filter.add("covid");
        filter.add("bibi");
        filter.add("sm0lan");
        filter.add("fuck");
        filter.add("barak");
        filter.add("chp");
        filter.add("studio");
    }


    public void start(int connectionId, Connections connections) {this.id=connectionId;}

    public void process(Object message) {
        MessageImpl msg=(MessageImpl) message;
        short opcode = msg.getOpCode();
        if (opcode == 1) {
            Register r = (Register) msg;
            synchronized (dt) {
                if (dt.isRegistered(r.getUserName()) || r.getPassword().equals(""))
                    sendError((short) 1);
                else {
                    dt.register(new Client(r.getUserName(),r.getPassword(),r.getbDay()));
                    sendAck((short) 1);
                }
            }
        }
        else if(opcode == 2){
            Login l = (Login) msg;
            synchronized (dt){
                if(l.getUserName().equals("") || l.getPassword().equals("") || !dt.isRegistered(l.getUserName()) ||
                        !dt.isPassword(l.getUserName(),l.getPassword()) || dt.isIn(l.getUserName()) ||
                        dt.NameUsingId(id)!=null || l.getCaptcha().equals("0")){
                    sendError((short) 2);
                }
                else{
                    dt.login(dt.getClient(l.getUserName()));
                    dt.loginUserId(id,l.getUserName());
                    sendAck((short)2);
                    for(Pending m : dt.getClient(l.getUserName()).getPendingMsgs()) {
                        sendNotification(dt.getClient(l.getUserName()), dt.getClient(m.getSender()), m.getType(), m.getContent());
                    }
                    dt.getClient(l.getUserName()).getPendingMsgs().clear();
                    }
                }
            }
        else if (dt.isIn(dt.NameUsingId(id))){
            String username = dt.NameUsingId(id);
                if(opcode==3){
                    synchronized (dt){
                        if(username == null || username.length()==0 || !dt.isIn(username)){
                            sendError((short) 3);
                        }
                        else{
                            dt.logout(dt.getClient(username));     //logs out
                            dt.logoutUserId(username);
                            if(sendAck(opcode)){
                                con.disconnect(id);
                            }
                        }
                    }
                }
                else if (opcode==4){   //follow/unfollow
                    FollowUnfollow f = (FollowUnfollow)msg;
                    if(dt.isIn(username)) {        //checks if he is logged in
                        synchronized (dt){
                            if(f.getFollowOrUnfollow()==0){ //follow
                                if(dt.isRegistered(f.getUsername()) && !dt.getClient(username).getBlocked().contains(f.getUsername()) &&
                                        !dt.getClient(f.getUsername()).getBlocked().contains(username) &&
                                        dt.follow(dt.getClient(username),dt.getClient(f.getUsername()))) {
                                    ackMsg tmp = new ackMsg(opcode, f.getUsername());
                                    tmp.setFollowORunfollow(0);
                                    con.send(id, tmp);
                                }
                                else
                                    sendError(opcode);
                            }
                            else{                       //unfollow
                                if(dt.unfollow(dt.getClient(username),dt.getClient(f.getUsername()))) {
                                    ackMsg tmp = new ackMsg(opcode, f.getUsername());
                                    tmp.setFollowORunfollow(1);
                                    con.send(id, tmp);
                                }
                                else sendError(opcode);
                            }
                        }
                    }
                }
                else if(opcode==5){                         //post
                    Post p = (Post)msg;
                    if(dt.isIn(username)){           //if the user is logged in
                        synchronized (dt){
                            for(String st : dt.getClient(username).getFollowers()){
                                if(dt.isIn(st)){                //if follower is logged in
                                    sendNotification(dt.getClient(st),dt.getClient(username),'1',p.getContent());
                                }
                                else{
                                    dt.getClient(st).getPendingMsgs().add(new Pending(username,p.getContent(),st,'1'));
                                }
                            }
                            for(String st : p.getTaggedNames()){
                                if(!dt.getClient(st).getBlocked().contains(username) && !dt.getClient(username).getBlocked().contains(st) && !dt.getClient(username).getFollowers().contains(st)) {
                                    if (dt.isIn(st)) {                //if follower is logged in
                                        sendNotification(dt.getClient(st), dt.getClient(username), '1', p.getContent());
                                    } else {
                                        dt.getClient(st).getPendingMsgs().add(new Pending(username, p.getContent(), st, '1'));
                                    }
                                }
                            }
                            dt.addPostPM(new Pending(username,p.getContent(),'1'));
                            dt.getClient(username).incrementNumOfPosts();
                            sendAck(opcode);
                        }
                    }
                    else       //if the user is logged out
                        sendError(opcode);
                }    // done handling of 'post'

                else if (opcode==6){    //PM
                    PM p = (PM) msg;
                    if(dt.isIn(username) && dt.isRegistered(p.getDstUserName()) &&
                            dt.getClient(username).getFollowing().contains(p.getDstUserName())){
                        for(String st : filter){
                            if(p.getContent().contains(st))
                                p.setContent(p.getContent().replaceAll(st,"<filtered>"));   //filter
                        }
                        if(dt.isIn(p.getDstUserName())){                //if follower is logged in
                            sendNotification(dt.getClient(p.getDstUserName()),dt.getClient(username),'0',p.getContent());
                        }
                        else{
                            dt.getClient(p.getDstUserName()).getPendingMsgs().add(new Pending(username,p.getContent(),p.getDstUserName(),'0'));
                        }
                        dt.addPostPM(new Pending(username,p.getContent(),'0'));
                        sendAck(opcode);
                    }
                    else{
                        sendError(opcode);
                    }
                }
                else if(opcode==7){    //logstat
                    Logstat l = (Logstat) msg;
                    if(dt.isIn(username)){
                        AtomicInteger count = new AtomicInteger();
                        dt.getOnlineMap().forEach((n,c)->{
                            String bd = c.getbDay();
                            char oneM = bd.charAt(4);     //getting MM out of 'DD-MM-YYYY'
                            char tenM = bd.charAt(3);     //getting MM out of 'DD-MM-YYYY'
                            int month = 0;
                            if (tenM != '0')
                                month = (tenM - '0') * 10;
                            if (oneM != 0)
                                month += (oneM - '0');
                            char oneD = bd.charAt(1);
                            char tenD = bd.charAt(0);
                            int day = 0;
                            if (tenD != '0')
                                day += (tenD - '0') * 10;
                            if (oneD != 0)
                                day += (oneD - '0');
                            String sub1 = bd.substring(6);       //getting YYYY out of 'DD-MM-YYYY'
                            int year = Integer.parseInt(sub1);
                            LocalDate d = LocalDate.of(year,month,day);
                            LocalDate now = LocalDate.now();
                            Period period = Period.between(d,now);
                            int age = period.getYears();
                            if(!c.getBlocked().contains(username) && !dt.getClient(username).getBlocked().contains(c.getUsername())) {
                                con.send(id, new ackMsg((short) 7, (short) age, (short) c.getNumOfPosts(),
                                        (short) c.getFollowers().size(), (short) c.getFollowing().size()));
                                count.addAndGet(1);
                            }
                        });
                    }
                    else {
                        sendAck(opcode);
                    }
                }
                else if(opcode==8){     //stat
                    Stat s = (Stat) msg;
                    if(dt.isIn(username)){
                        boolean b = false;
                        for(String c : s.getTaggedUsers()){
                            // already true   ||  not registered   ||   blocked
                            b = b || !dt.isRegistered(c) || dt.getClient(c).getBlocked().contains(username)
                                   || dt.getClient(username).getBlocked().contains(c);
                        }
                        if(b)
                            sendError(opcode);
                        else {
                            for (String tagged : s.getTaggedUsers()) {
                                String bd = dt.getClient(tagged).getbDay();
                                String sub1 = bd.substring(6);       //getting YYYY out of 'DD-MM-YYYY'
                                char oneM = bd.charAt(4);     //getting MM out of 'DD-MM-YYYY'
                                char tenM = bd.charAt(3);     //getting MM out of 'DD-MM-YYYY'
                                int month = 0;
                                if (tenM != '0')
                                    month = (tenM - '0') * 10;
                                if (oneM != 0)
                                    month += (oneM - '0');
                                char oneD = bd.charAt(1);
                                char tenD = bd.charAt(0);
                                int day = 0;
                                if (tenD != '0')
                                    day += (tenD - '0') * 10;
                                if (oneD != 0)
                                    day += (oneD - '0');
                                int year = Integer.parseInt(sub1);
                                LocalDate d = LocalDate.of(year, month, day);
                                LocalDate now = LocalDate.now();
                                Period period = Period.between(d, now);
                                int age = period.getYears();            //done calculating age
                                if (!dt.getClient(tagged).getBlocked().contains(username)) {
                                   con.send(id, new ackMsg((short) 7, (short) age,
                                            (short) dt.getClient(tagged).getNumOfPosts(),
                                            (short) dt.getClient(tagged).getFollowers().size(),
                                            (short) dt.getClient(tagged).getFollowing().size()));
                                }
                            }
                        }
                    }
                    else sendError(opcode);
                }
                else if(opcode==12) {                //block
                    Block b = (Block) msg;
                    synchronized (dt) {
                        if (dt.isIn(username) && dt.isRegistered(b.getBlockedUser()) &&
                        !dt.getClient(username).getBlocked().contains(b.getBlockedUser()) && con.send(id, new ackMsg(opcode,b.getBlockedUser()))) {
                            //blocker - following
                            dt.getClient(username).getFollowing().remove(b.getBlockedUser());

                            //blocker - followers
                            dt.getClient(username).getFollowers().remove(b.getBlockedUser());

                            //blocking - following
                            dt.getClient(b.getBlockedUser()).getFollowing().remove(username);

                            //blocking - followers
                            dt.getClient(b.getBlockedUser()).getFollowers().remove(username);

                            dt.getClient(username).getBlocked().add(b.getBlockedUser());

                        } else sendError(opcode);
                    }
                }
        }
        else
            sendError(opcode);
    }
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public void sendError(short op){
        con.send(id,new Error_spl(op));
    }

    public boolean sendAck(short op){
        return con.send(id,new ackMsg(op));
    }

    public void sendNotification(Client receiver,Client sender,char type,String msg){
        con.send(dt.IdUsingName(receiver.getUsername()),new Notification(type,sender.getUsername(),msg));
    }
}
