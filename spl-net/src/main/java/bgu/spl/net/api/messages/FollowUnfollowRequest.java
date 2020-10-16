package bgu.spl.net.api.messages;


import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.User;

import java.util.Vector;

public class FollowUnfollowRequest implements Message{

   private String userName;
    private Vector<String> userNameList;
    private int follow;
    private ConnectionsImpl<Message> connections;


    public FollowUnfollowRequest(String userName,Vector<String> userNameList,int follow){
        this.follow=follow;
        this.userName=userName;
        this.userNameList=userNameList;

    }
    /**
     * Adds or Removes userNames from the Follow/Unfollow List of userName.
     * Returns a list of usernames which we did follow/unfollow successfully.
     * size 0 of the returned list means that we didn't add or remove nothing
     **/
    @Override
    public void execute(int connectionId,DataBase dataBase) {
        Message response;
        User tempUser = dataBase.getUserByName(this.userName);
        int counterFollowUnFollow=0;
        String successfullFollowUnFollowStringSepertedByZero="";
        if(tempUser!=null && dataBase.isActive(this.userName)) {
            if (this.follow == 0) {
                for (String userNameToFollow : userNameList) {
                    if (dataBase.isUserExist(userNameToFollow)) {
                        if (!tempUser.getFollowing().contains(userNameToFollow)) {
                            User userToFollow = dataBase.getUserByName(userNameToFollow);
                            tempUser.addFollowing(userNameToFollow);
                            userToFollow.addFollower(this.userName);
                            successfullFollowUnFollowStringSepertedByZero =successfullFollowUnFollowStringSepertedByZero+userNameToFollow + "\0";
                            counterFollowUnFollow++;
                        }
                    }
                }
            }
            else {
                for (String userNameToUnFollow : userNameList) {
                    if (dataBase.isUserExist(userNameToUnFollow)) {
                        if (tempUser.getFollowing().contains(userNameToUnFollow)) {
                            User userToUnFollow = dataBase.getUserByName(userNameToUnFollow);
                            tempUser.removeFollowing(userNameToUnFollow);
                            userToUnFollow.removeFollower(this.userName);
                            successfullFollowUnFollowStringSepertedByZero =successfullFollowUnFollowStringSepertedByZero+userNameToUnFollow + "\0";
                            counterFollowUnFollow++;
                        }
                    }
                }
            }

            if (counterFollowUnFollow>0) {
                response = new Response(10, ("4" + follow + successfullFollowUnFollowStringSepertedByZero));//ACK message
                ((Response) response).setNumOfUsers(""+counterFollowUnFollow);
            }
            else {
                response = new Response(11, "4");//Error message
            }
        } //means user is not logged in
        else
            response=new Response(11,"4");//Error message

        connections.send(connectionId,response);
    }

    @Override
    public void setConnections(ConnectionsImpl<Message> connections)
    {
        this.connections = connections;
    }



}
