package bgu.spl.net.api.bidi;
import bgu.spl.net.srv.bidi.ConnectionHandler;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {

    private ConcurrentHashMap<Integer, ConnectionHandler<T>> connectionsHashMap; //Key: ID of active client, Value: Connection Handler

    public ConnectionsImpl() {
        connectionsHashMap = new ConcurrentHashMap<>();
    }

    public boolean send(int connectionId, T msg) {
        ConnectionHandler<T> tempChClient = connectionsHashMap.get(connectionId);
        if (tempChClient != null) {
            tempChClient.send(msg);
            return true;
        }
        return false;
    }

    public void broadcast(T msg) {
        Set<Integer> setIds = connectionsHashMap.keySet();
        for (Integer id : setIds){
            send(id, msg);
        }
    }

    public void disconnect(int connectionId) {

        connectionsHashMap.remove(connectionId);
    }

    public void addConnectionId(int connectionId, ConnectionHandler<T> handler){
            this.connectionsHashMap.putIfAbsent(connectionId,handler);
    }
}