# TCP-Server-Client-Social-Network
Supports Reactor & Thread-Per-Client design patterns using Generics, includes Socket Programming &amp; Encoder-Decoder implementation.

To Run The Server:
1) open spl-net folder	
2) open terminal
3) mvn clean compile
4) TPC:
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.TPCMain" -Dexec.args="7777"
4) Reactor:
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.ReactorMain" -Dexec.args="7777 4"

To Run a Client:

1) open Boost_Echo_Client
2) open terminal
3) make
4) cd bin
5) ./BGSclient 127.0.0.1 7777

Supported Commands:

1) REGISTER <User Name> <Password>
2) LOGIN <User Name> <Password>
3) LOGOUT
4) USERLIST
5) STAT <User Name>
6) POST <Message>
7) PM <User Name> <Message>
8) FOLLOW 0 <Num of users to follow> <USERLIST>
9) FOLLOW 1 <Num of users to unfollow> <USERLIST>
