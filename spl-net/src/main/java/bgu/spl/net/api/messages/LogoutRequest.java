package bgu.spl.net.api.messages;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.srv.DataBase;

public class LogoutRequest implements Message {

   private String userName;
    private Connections<Message> connections;
    private boolean isLogoutSucceeded;

    public LogoutRequest (String userName){
        this.userName=userName;

    }

    /**
     * Logout a user
     */
    @Override
    public void execute(int connectionId,DataBase dataBase) {
        Message response;
        isLogoutSucceeded = dataBase.logMeOut(connectionId);

        if(isLogoutSucceeded)
            response=new Response(10,"3");//ACK message

        else {
            response = new Response(11, "3");//Error message
        }
        connections.send(connectionId,response);
    }

    @Override
    public void setConnections(ConnectionsImpl<Message> connections) {
        this.connections=connections;

    }
    public boolean isLogoutSucceeded() {
        return isLogoutSucceeded;
    }

}
