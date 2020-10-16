package bgu.spl.net.api.bidi;

import bgu.spl.net.api.messages.*;
import bgu.spl.net.srv.DataBase;

public class BidiMessagingProtocolImpl<T> implements BidiMessagingProtocol<T> {

    private ConnectionsImpl<Message> connections;
    private DataBase dataBase;
    private int connectionId;
    private boolean shouldTerminate=false;

    public BidiMessagingProtocolImpl(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    /**
     * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
     **/
    public void start(int connectionId, Connections<T> connections) {
        this.connections = (ConnectionsImpl<Message>)connections;
        this.connectionId=connectionId;

    }



    public void process(T message) {
        ((Message)message).setConnections(connections);
        ((Message)message).execute(connectionId,dataBase);

        if (message instanceof LogoutRequest){
            if(((LogoutRequest)message).isLogoutSucceeded())
                shouldTerminate=true;
        }

    }


    /**
     * @return true if the connection should be terminated
     */
    public boolean shouldTerminate(){

        return shouldTerminate;
    }
}
