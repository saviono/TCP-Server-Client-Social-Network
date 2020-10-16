package bgu.spl.net.api.messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.User;


public class StatsRequest implements Message {
     private String userName;
    private String myUserName;
    private Connections<Message> connections;

    public StatsRequest(String myUserName,String userName) {
        this.userName=userName;
        this.myUserName=myUserName;
    }

    @Override
    public void execute(int connectionId, DataBase dataBase) {
        Response response;
        //boolean isMyUserActive = dataBase.isActiveByConnectionID(connectionId);
        boolean isMyUserActive = dataBase.isActive(myUserName);
        User tempUser = dataBase.getUserByName(this.userName);
        if(tempUser!=null && isMyUserActive) {
            String postNum=""+tempUser.getNumOfPosts();
            String followersNum=""+tempUser.getFollowers().size();
            String followingNum=""+tempUser.getFollowing().size();
            String[] array={postNum,followersNum,followingNum};
            response=new Response(10,"8");//Ack message
            response.setStat(array);

        }
        else
            response=new Response(11,"8");//Error message
        connections.send(connectionId,response);

    }

    @Override
    public void setConnections(ConnectionsImpl<Message> connections) {
        this.connections=connections;

    }

}
