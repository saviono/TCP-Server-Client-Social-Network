package bgu.spl.net.api.messages;

import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.User;

public class UserListRequest implements Message {
    String userName;
    private ConnectionsImpl<Message> connections;

    public UserListRequest(String userName) {
        this.userName=userName;
    }

    @Override
    public void execute(int connectionId, DataBase dataBase) {
        Message response;
        User tempUser = dataBase.getUserByName(this.userName);
        if(tempUser!=null && dataBase.isActive(userName)) {
            response=new Response(10,"7"+dataBase.getUsersByNamesQueueSeperetedByZero());//ACK massage
            int size=dataBase.getRegisteredUsersCounter();
            ((Response) response).setNumOfUsers(""+size);

        }
        else
            response=new Response(11,"7");//Error massage
        connections.send(connectionId,response);

    }

    @Override
    public void setConnections(ConnectionsImpl<Message> connections) {

            this.connections = connections;

    }

}
