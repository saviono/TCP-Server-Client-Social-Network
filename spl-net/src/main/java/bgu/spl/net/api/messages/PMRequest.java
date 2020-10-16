package bgu.spl.net.api.messages;

import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.User;

public class PMRequest implements Message {
    private String userName;
    private String userNameToSend;
    private String content;
    private ConnectionsImpl<Message> connections;

    public PMRequest(String userName,String userNameToSend,String content){
        this.content=content;
        this.userName=userName;
        this.userNameToSend=userNameToSend;

    }


    @Override
    public void execute(int connectionId,DataBase dataBase) {
        Message response;
        User tempUser = dataBase.getUserByName(this.userName);
        if (tempUser != null && dataBase.isActive(this.userName)) {
            boolean isUserToSendPostRegistered = dataBase.isUserExist(userNameToSend);

            if (isUserToSendPostRegistered) {
                tempUser.addPostPmVector(content, 1);
                Response notification = new Response(9, ("\0" + userName + "\0" + content + "\0"));//notification message
                User userToSendPost = dataBase.getUserByName(userNameToSend);
                synchronized (userToSendPost) {//Sync with logMeOut function on database in order to prevent a sitiouation that a user who logs out will not the PM
                    if (dataBase.isActive(userNameToSend)) {
                        int idToSendPost = dataBase.getHashMapByUserNameToIdConnection().get(userToSendPost.getUserName());
                        connections.send(idToSendPost, notification);
                    } else {
                        userToSendPost.addNotification(notification);
                    }
                }
                response = new Response(10, "6" );//ACK message
            }
            else {
                response = new Response(11, "6");//Error message because UserToSendPost is not Registered
            }
        }
        else{
            response = new Response(11, "6");
        }

        connections.send(connectionId, response);

    }

    @Override
    public void setConnections(ConnectionsImpl<Message> connections) {
        this.connections=connections;
    }
}
