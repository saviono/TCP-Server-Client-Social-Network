package bgu.spl.net.api.messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.srv.DataBase;

public interface Message {

    void execute (int connectionId,DataBase dataBase);

    void setConnections(ConnectionsImpl<Message> connections);



}
