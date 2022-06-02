package bgu.spl.net.api.bidi;

import bgu.spl.net.api.bidi.Connections;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T>{

    private ConcurrentHashMap<Integer, bgu.spl.net.srv.bidi.ConnectionHandler<T>> handlers=new ConcurrentHashMap<>();

    public static ConnectionsImpl getInstance(){
        if (ConnectionsImplHolder.instance==null)
            ConnectionsImplHolder.instance=new ConnectionsImpl();
        return ConnectionsImplHolder.instance;
    }

    private static class ConnectionsImplHolder{
        private static ConnectionsImpl instance;
    }


    public boolean send(int connectionId, T msg) {
        boolean result=false;
        if(handlers.containsKey(connectionId)){
            handlers.get(connectionId).send(msg);
            result=true;
        }
        return result;
    }

    public void broadcast(T msg) {
        handlers.forEach((k,v)->{
            v.send(msg);
        });
    }


    public void disconnect(int connectionId) {
        handlers.remove(connectionId);
    }
    public void startCH(int id, bgu.spl.net.srv.bidi.ConnectionHandler<T> h) {
        handlers.put(id,h);
    }

}