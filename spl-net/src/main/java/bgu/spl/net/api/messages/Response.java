package bgu.spl.net.api.messages;

import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.srv.DataBase;

public class Response implements Message {

    private int opcode;
    private String content;
    private String numOfUsers;
    private String[] stat;


    public Response(int opcode,String content) {
        this.opcode=opcode;
        this.content=content;
    }

    @Override
    public void execute(int connectionId, DataBase dataBase) {

    }

    @Override
    public void setConnections(ConnectionsImpl<Message> connections) {

    }

    public String getContent () {
        return content;
    }

    public int getOpcode() {
        return opcode;
    }

    public void setNumOfUsers(String numOfUsers) {
        this.numOfUsers = numOfUsers;
    }

    public String getNumOfUsers() {
        return numOfUsers;
    }

    public String[] getStat() {
        return stat;
    }

    public void setStat(String[] stat) {
        this.stat = stat;
    }


}
