# TCP-Server-Client-Social-Network
Supports Reactor &amp; Thread-Per-Client design patterns using Generics, includes Socket Programming &amp; Encoder-Decoder implementation.

Supports Reactor & Thread-Per-Client design patterns using Generics, includes Socket Programming & Encoder-Decoder implementation.

To Run The Server:

open spl-net folder
open terminal
mvn clean compile

TPC:
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.TPCMain" -Dexec.args="7777"

Reactor:
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.ReactorMain" -Dexec.args="7777 4"

To Run a Client:

open Boost_Echo_Client
open terminal
make
cd bin
./BGSclient 127.0.0.1 7777

Supported Commands:

REGISTER <User Name> <Password>
LOGIN <User Name> <Password>
LOGOUT
USERLIST
STAT <User Name>
POST <Message>
PM <User Name> <Message>
FOLLOW 0 <Num of users to follow> <USERLIST>
FOLLOW 1 <Num of users to unfollow> <USERLIST>
