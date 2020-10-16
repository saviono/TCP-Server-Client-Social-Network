package bgu.spl.net.impl.BGSServer;


import bgu.spl.net.api.bidi.MessageEncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.Server;


public class TPCMain {

    public static void main(String[] args){
        DataBase dataBase=new DataBase();
        int port=Integer.parseInt(args[0]);
      //  int port=Integer.parseInt("7777");

        Server server = Server.threadPerClient( //Creates an instance of BaseServer, and runs the server function
                port,
                ()->new BidiMessagingProtocolImpl<>(dataBase),
                ()->new MessageEncoderDecoderImpl());

                server.serve();



    }

}
