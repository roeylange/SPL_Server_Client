package bgu.spl.net.api;

import bgu.spl.net.api.Messages.Pending;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.srv.Client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class DataManager {

    private ConcurrentHashMap<Integer,String> userNameMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String,Integer> S_to_I = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Client> clientMap = new ConcurrentHashMap<String, Client>();
    private ConcurrentHashMap<String, Client> onlineMap = new ConcurrentHashMap<String, Client>();
    private LinkedBlockingQueue<Pending> Posts_PMs = new LinkedBlockingQueue<>();

    public static DataManager getInstance(){
        if (DataManager.DataManagerHolder.instance==null)
            DataManager.DataManagerHolder.instance=new DataManager();
        return DataManager.DataManagerHolder.instance;
    }

    private static class DataManagerHolder{
        private static DataManager instance;
    }

    public void register(Client c){
        clientMap.put(c.getUsername(),c);
    }

    public void login(Client c){
        onlineMap.put(c.getUsername(),c);
        c.setOnline(true);
    }
    public void loginUserId(int id, String userName){
        S_to_I.put(userName,id);
        userNameMap.put(id,userName);
    }
    public void logout(Client c){
        onlineMap.remove(c.getUsername());
        c.setOnline(false);
    }

    public void logoutUserId(String userName){
        int id = S_to_I.get(userName);
        S_to_I.remove(userName);
        userNameMap.remove(id);
    }

    public boolean follow(Client follower, Client following){
        boolean ans=false;
        if(!follower.getBlocked().contains(following) || !following.getBlocked().contains(follower)){
            if (!follower.getFollowing().contains(following.getUsername())) {
                follower.addFollowing(following.getUsername());
                ans = true;
            }
            if (!following.getFollowers().contains(follower.getUsername())) {
                following.addFollower(follower.getUsername());
                ans = true;
            }
        }
        return ans;
    }
    public boolean unfollow(Client follower, Client following){
        boolean ans=false;
        System.out.println("im in the unfollow function");
        if(follower.getFollowing().contains(following.getUsername())) {
            follower.removeFollowing(following.getUsername());
            ans=true;
        }
        if(following.getFollowers().contains(follower.getUsername())) {
            following.removeFollower(follower.getUsername());
            ans=true;
        }
        return ans;
    }

    public void addPostPM(Pending p){
        Posts_PMs.add(p);
    }

    public boolean isRegistered(String userName){return clientMap.containsKey(userName);}

    public boolean isPassword(String userName,String password){return clientMap.get(userName).getPassword().equals(password);}

    public boolean isIn(String userName){
        if(userName!=null)
            return onlineMap.containsKey(userName);
        return false;
    }

    public Client getClient(String userName){return clientMap.get(userName);}

    public int IdUsingName(String name){return S_to_I.get(name);}

    public String NameUsingId(int id){return userNameMap.get(id);}

    public ConcurrentHashMap<String, Client> getOnlineMap() { return onlineMap; }
}
