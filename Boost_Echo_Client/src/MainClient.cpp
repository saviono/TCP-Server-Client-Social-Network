//
// Created by saviono@wincs.cs.bgu.ac.il on 12/29/18.
//

#include <stdlib.h>
#include "../include/connectionHandler.h"
#include <iostream>
#include <thread>

using namespace std;

class TaskWrite {
private:
    ConnectionHandler &connectionHandler;
public:
    TaskWrite(ConnectionHandler &connectionHandler) : connectionHandler(connectionHandler) {}

    void operator()() {

        while (!connectionHandler.isLoggedOut()) {
            const short bufsize = 1024;
            char buf[bufsize];
            std::cin.getline(buf, bufsize);
            std::string line(buf);
            int len = line.length();
            int lenTemp = line.length();
            string contentForPost="";
            string contentForPM="";

            vector<string> lineVector;
            char opcodeBytesArr[2];


            while (lenTemp > 0) {
                int i = line.find_first_of(' ');
                if((lineVector.size()==1)&&(lineVector[0]=="POST"))
                    contentForPost=contentForPost+line;
                if((lineVector.size()==2)&&(lineVector[0]=="PM"))
                    contentForPM=contentForPM+line;

                if (i==-1){
                    lineVector.push_back(line);
                    lenTemp=0;
                } else {

                    string word = line.substr(0, i);
                    lineVector.push_back(word);
                    line = line.substr(i + 1, len);
                    lenTemp = line.length();
                }
            }

            if ("LOGIN" == lineVector[0] || "REGISTER" == lineVector[0]) {
                if ("REGISTER" == lineVector[0]) {
                    connectionHandler.shortToBytes(1, opcodeBytesArr);

                }
                else {
                    connectionHandler.shortToBytes(2, opcodeBytesArr);
                }
                connectionHandler.sendBytes(opcodeBytesArr, 2);

                string userName = lineVector[1];
                connectionHandler.sendLine(userName);
                string password = lineVector[2];
                connectionHandler.sendLine(password);

            }
            if ("LOGOUT" == lineVector[0]) {
                string temp="";
                connectionHandler.shortToBytes(3, opcodeBytesArr);
                connectionHandler.sendBytes(opcodeBytesArr, 2);
                while(!connectionHandler.getLocker()){

                }

            }
            if ("FOLLOW" == lineVector[0]) {
                connectionHandler.shortToBytes(4, opcodeBytesArr);
                connectionHandler.sendBytes(opcodeBytesArr, 2);
                char followBytesArr[1];
                if (lineVector[1] == "0")
                    followBytesArr[0]= 0;
                else
                    followBytesArr[0]= 1;
                connectionHandler.sendBytes(followBytesArr, 1);
                int numOfUsers = stoi(lineVector[2]);
                connectionHandler.shortToBytes(numOfUsers, opcodeBytesArr);
                connectionHandler.sendBytes(opcodeBytesArr, 2);
                for (int i = 3; i < (int)lineVector.size(); i++) {
                    connectionHandler.sendLine(lineVector[i]);
                }
            }
            if ("POST" == lineVector[0]) {
                connectionHandler.shortToBytes(5, opcodeBytesArr);
                connectionHandler.sendBytes(opcodeBytesArr, 2);
                connectionHandler.sendLine(contentForPost);

            }
            if ("PM" == lineVector[0]) {
                connectionHandler.shortToBytes(6, opcodeBytesArr);
                connectionHandler.sendBytes(opcodeBytesArr, 2);
                string userName = lineVector[1];
                connectionHandler.sendLine(userName);
                connectionHandler.sendLine(contentForPM);
            }
            if ("USERLIST" == lineVector[0]) {
                connectionHandler.shortToBytes(7, opcodeBytesArr);
                connectionHandler.sendBytes(opcodeBytesArr, 2);
            }
            if ("STAT" == lineVector[0]) {
                connectionHandler.shortToBytes(8, opcodeBytesArr);
                connectionHandler.sendBytes(opcodeBytesArr, 2);
                string userName = lineVector[1];
                connectionHandler.sendLine(userName);
            }


        }
    }


};

class TaskRead {
private:
    ConnectionHandler & connectionHandler;
public:

    TaskRead(ConnectionHandler & connectionHandler) : connectionHandler(connectionHandler) {}

    void operator()() {

        while (!connectionHandler.isLoggedOut()){
            char opcodeBytesArr2[2];
            connectionHandler.getBytes(opcodeBytesArr2, 2);
            short opcode = connectionHandler.bytesToShort(opcodeBytesArr2);

            char msgOpcodeBytesArr[2];
            short msgOpcode;
            string line;

            if (opcode == 11) {
                connectionHandler.getBytes(msgOpcodeBytesArr, 2);
                msgOpcode= connectionHandler.bytesToShort(msgOpcodeBytesArr);
                if(msgOpcode==3)
                    connectionHandler.setLocker();

                std::cout << "ERROR " << msgOpcode << std::endl;

                opcode=0;
                msgOpcode=0;

            }
            if (opcode == 10) {
                connectionHandler.getBytes(msgOpcodeBytesArr, 2);
                msgOpcode= connectionHandler.bytesToShort(msgOpcodeBytesArr);

                if ((msgOpcode == 1) || (msgOpcode == 2) || (msgOpcode == 5) || (msgOpcode == 6) ) {//ACK of register | Ack of Login | ACK of Post | ACK of PM |

                    std::cout << "ACK " << msgOpcode << std::endl; //todo - should we need the line?
                    msgOpcode = 0;
                    opcode = 0;
                }


                if (msgOpcode == 3) {//ACK Of logout
                    std::cout << "ACK " << msgOpcode << std::endl;
                    connectionHandler.setLoggedOut();
                    connectionHandler.setLocker();
                    connectionHandler.close();
                    break;
                }

                if ((msgOpcode == 4) || (msgOpcode == 7)) {//ACK of followUnfollow | ACK of Userlist
                    char numOfUsersBytesArr[2];
                    connectionHandler.getBytes(numOfUsersBytesArr, 2);
                    short numOfUsers = connectionHandler.bytesToShort(numOfUsersBytesArr);
                    string ans = "ACK " + to_string((int)msgOpcode) +" "+ to_string((int)numOfUsers)+" ";
                    for (int i = 0; i < numOfUsers; i++) {
                        connectionHandler.getLine(line);
                            int len=line.length();
                            line.resize(len-1);
                        ans=ans+line+" ";
                        line= "";
                    }
                    ans.resize(ans.length()-1);
                    cout << ans << endl;
                    msgOpcode = 0;
                    opcode = 0;

                }

                if (msgOpcode==8) {//ACK of STAT
                    char numOfPostsBytesArr[2];
                    connectionHandler.getBytes(numOfPostsBytesArr, 2);
                    short numOfPosts = connectionHandler.bytesToShort(numOfPostsBytesArr);
                    char numOfFollowersBytesArr[2];
                    connectionHandler.getBytes(numOfFollowersBytesArr,2);
                    short numOfFollowers = connectionHandler.bytesToShort(numOfFollowersBytesArr);
                    char numOfFollowingArr[2];
                    connectionHandler.getBytes(numOfFollowingArr, 2);
                    short numOfFollowing = connectionHandler.bytesToShort(numOfFollowingArr);
                    std::cout << "ACK " << msgOpcode << " " << numOfPosts << " " << numOfFollowers << " " <<  numOfFollowing << std::endl;
                }

            }

            if (opcode == 9) {
                string content="";
                string userName="";
                string pmOrPost="";
                line="";
                char pmOrPostBytesArr[1];
                connectionHandler.getBytes(pmOrPostBytesArr,1);
                if(pmOrPostBytesArr[0]=='\0')
                    pmOrPost = "PM";
                else
                    pmOrPost = "Public";
                connectionHandler.getLine(line);
                userName=line;
                userName.resize(userName.length()-1);
                connectionHandler.getLine(content);
                content.resize(content.length()-1);
                std::cout << "NOTIFICATION " << pmOrPost <<" "<< userName<<" " << content << std::endl;
                opcode = 0;

            }

        }

    }


};

int main(int argc, char *argv[]) {

    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    TaskWrite taskWrite(ref(connectionHandler));
    TaskRead taskRead(ref(connectionHandler));

    std::thread thWrite(std::ref(taskWrite)); // we use std::ref to avoid creating a copy of the Task object
    std::thread thRead(std::ref(taskRead));

    thWrite.join();
    thRead.join();

    return 0;
}


