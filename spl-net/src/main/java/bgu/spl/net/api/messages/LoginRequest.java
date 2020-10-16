package bgu.spl.net.api.messages;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.User;

import java.util.Queue;

public class LoginRequest implements Message {

    private String userName;
    private String password;
    private ConnectionsImpl<Message> connections;

    public LoginRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    /**
     * Logs in a user
     */
    @Override
    public void execute( int connectionId, DataBase dataBase) {
        Message response;
            boolean goActive = dataBase.addActiveUser(connectionId, userName, password);
            if (dataBase.getUserByName(this.userName) != null) {

                synchronized (dataBase.getUserByName(this.userName)) { //Sync on the user with logMeOut - if no sync so if someone tries to logout first, maybe he wont get all the notifications
                    User tempUser = dataBase.getUserByName(this.userName);
                    if (goActive) {
                        response = new Response(10, "2");//ACK message
                        Queue<Response> notificationQueue = tempUser.getNotificationQueue();
                        connections.send(connectionId, response);
                        while (!notificationQueue.isEmpty()) {
                            connections.send(connectionId, notificationQueue.poll());
                        }
                    } else {
                        response = new Response(11, "2");//Error message
                        connections.send(connectionId, response);
                    }
                }
            }
            else {
                response = new Response(11, "2");//Error message
                connections.send(connectionId, response);
            }
        }
    public void setConnections(ConnectionsImpl<Message> connections)
    {
        this.connections = connections;
    }

}
