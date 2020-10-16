package bgu.spl.net.api.messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.User;

public class RegisterRequest implements Message {

    private String userName;
    private String password;
    private ConnectionsImpl<Message> connections;

    public RegisterRequest ( String userName, String password){
        this.userName=userName;
        this.password=password;
    }

    /**
     * Tries register a new user.
     * Returns false if already registered, else false
     */
    @Override
    public void execute(int connectionId,DataBase dataBase) {
        Message response;
            //boolean isAdded = dataBase.addUser(connectionId,userName,password);
        boolean isAdded = dataBase.addUser(userName,password);
            if(isAdded){
                response= new Response(10,"1");
            }
            else {
                response= new Response(11,"1");
            }

        connections.send(connectionId,response);
    }

    public void setConnections(ConnectionsImpl<Message> connections) {
        this.connections = connections;
    }

}
