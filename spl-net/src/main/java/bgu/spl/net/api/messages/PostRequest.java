package bgu.spl.net.api.messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.User;

import java.util.Vector;

public class PostRequest implements Message{

    private String userName;
    private Vector<String> taggedUsers;
    private String content;
    private Connections<Message> connections;

    public PostRequest(String userName,String content){  //TODO: send here an empty vector or vector with taggs
        this.userName=userName;
        this.content=content;
        this.taggedUsers = new Vector<>();
        createTaggedUsersVector(content);
    }
    private void createTaggedUsersVector (String s){
        int startIndex;
        for (int i=0;i<s.length();i++){
            char c = s.charAt(i);
            startIndex=i+1;
            if (c=='@'&&i<s.length()){
                i++;
                while (c!=' '){
                   if(i==s.length()) {
                       this.taggedUsers.add(s.substring(startIndex, i));
                       return;
                   }
                    c=s.charAt(i);
                     i++;
                }
                this.taggedUsers.add(s.substring(startIndex,i-1));
            }
        }
    }


    @Override
    public void execute(int connectionId, DataBase dataBase) {
        /**
         * Sends post to all tagged users
         **/
        Response response;
            User tempUser = dataBase.getUserByName(this.userName);
            if(tempUser!=null && dataBase.isActive(this.userName)) {
                tempUser.addPostPmVector(content, 0);
                for (String user : taggedUsers) {
                    sendThePost(user,dataBase);
                }

                /**
                 * Sends post to all of the followers of userName
                 **/
                Vector<String> followers = tempUser.getFollowers();
                for (String user : followers) {
                    if(!taggedUsers.contains(user))
                      sendThePost(user,dataBase);
                }
                response=new Response(10,"5");//ACK message
            }
            else
                response=new Response(11,"5");//Error message

        connections.send(connectionId,response);
    }

    private void sendThePost(String user,DataBase dataBase){
        User userToSendPost = dataBase.getUserByName(user);
        if (userToSendPost != null) {
            Response notification = new Response(9, ("\1" + userName + "\0" + content + "\0"));//Notification message
            synchronized (userToSendPost) { //Sync with logMeOut function on database in order to prevent a sitiouation that a user who logs out will not the post - the problem is beacuse we ask isActive
                if (dataBase.isActive(userToSendPost.getUserName())) {
                    int idToSendPost = dataBase.getHashMapByUserNameToIdConnection().get(user);
                    connections.send(idToSendPost, notification);
                } else
                    userToSendPost.addNotification(notification);
            }
        }
    }

    @Override
    public void setConnections(ConnectionsImpl<Message> connections) {
        this.connections = connections;

    }

}