package bgu.spl.net.srv;

import bgu.spl.net.api.messages.Response;



import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class User {

    private String userName;
    private String password;
    private Vector<String> followers;
    private Vector<String> following;
    private Vector<String> postPmVector;
    private AtomicInteger numOfPosts;
    private AtomicInteger numOfPms;
    private Queue<Response> notificationQueue;


    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        followers = new Vector<>();
        following = new Vector<>();
        postPmVector = new Vector<>();
        this.notificationQueue = new ConcurrentLinkedDeque<>();
        numOfPosts = new AtomicInteger(0);
        numOfPms =new AtomicInteger(0);

    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Vector<String> getFollowers() {
        return followers;
    }

    public Vector<String> getFollowing() {
        return following;
    }

    public void addFollower(String userName) {
        this.followers.add(userName);
    }

    public void removeFollower(String userName) {
        this.followers.remove(userName);
    }

    public void addFollowing(String userName) {

        this.following.add(userName);
    }

    public void removeFollowing(String userName) {
        this.following.remove(userName);
    }

    public Queue<Response> getNotificationQueue() {
        return notificationQueue;
    }

    public void addNotification(Response message) {
        this.notificationQueue.add(message);
    }

    public Vector<String> getPostPmVector() {
        return postPmVector;
    }

    /**
     * postOrPm = 0 means it's a post.
     * postOrPm = 1 means it's a pm.
     **/
    public void addPostPmVector(String message, int postOrPm){
        this.postPmVector.add(message);
        if (postOrPm ==0)
            numOfPosts.incrementAndGet();
        else
            numOfPms.incrementAndGet();
    }

    public int getNumOfPosts() {
        return numOfPosts.get();
    }

    public int getNumOfPms() {
        return numOfPms.get();
    }


}
