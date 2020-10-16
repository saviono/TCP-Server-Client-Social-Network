package bgu.spl.net.api.bidi;

import bgu.spl.net.api.bidi.MessageEncoderDecoder;
import bgu.spl.net.api.messages.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Vector;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {

    private byte[] bytes = new byte[1 << 10];
    private int len = 0, start = 2;
    private short opcode ;
    private int numOfZero = 0;
    private String userName="", password ="", content="";
    private String myUserName="";
    private String myUserNameForRegister="";//to know all the time who is send request
    private int follow= 0;
    private int numOfUsers=-1;
    private Vector<String> userNameList=new Vector<>();
    private String myUserNameForLogin="";

    @Override
    public Message decodeNextByte(byte nextByte) {
        pushByte(nextByte);
        if (len == 2) {
            byte[] tempBytesArray = new byte[2];
            tempBytesArray[0] = bytes[0];
            tempBytesArray[1] = bytes[1];
            opcode = bytesToShort(tempBytesArray);

        }
        switch (opcode) {

            case 1:
                return registerOrLogin(1, nextByte);
            case 2:
                return registerOrLogin(2, nextByte);
            case 3:
                initiateValues();
                return new LogoutRequest(myUserName);
            case 4:
                return followUnFollow(nextByte);
            case 5:
                return post(nextByte);
            case 6:
                return pm(nextByte);
            case 7:
                initiateValues();
                return new UserListRequest(myUserName);
            case 8:
                return stat(nextByte);
        }



        return null;
    }


    private Message registerOrLogin(int c,byte nextByte) {

        if (nextByte == '\0') {
            numOfZero++;
            if (numOfZero == 1) {
                if(c==1) {
                 this.myUserNameForRegister = popString();

                }
                else if(c==2) {
                    myUserNameForLogin=popString();

                }
            }
            if (numOfZero == 2) {
                this.password = popString();

                initiateValues();
                if(c==1)
                    return new RegisterRequest(myUserNameForRegister,password);
                else if (c==2)
                    return new LoginRequest(myUserNameForLogin,password);
            }
        }

        return null;
    }

    private Message followUnFollow(byte nextByte){
        if(len==3) {
            follow=bytes[2];
        }

        if(len==5){
            byte[] tempBytesArray = new byte[2];
            tempBytesArray[0] = bytes[3];
            tempBytesArray[1] = bytes[4];
            numOfUsers=bytesToShort(tempBytesArray);
            start=5;

        }

        if (numOfZero < numOfUsers) {
            if (nextByte == '\0') {
                numOfZero++;
                String userName = popString();
                userNameList.add(userName);
            }
        }
        if (numOfZero == numOfUsers) {
            Message output = new FollowUnfollowRequest(myUserName, this.userNameList, this.follow);
            initiateValues();
            userNameList=new Vector<>();
            return output;
        }

        return null;
    }

    private Message post(byte nextByte){
        if (nextByte == '\0') {
            content = popString();
            initiateValues();
            return new PostRequest(myUserName,content);
        }
        return null;
    }

    private Message pm (byte nextByte){
        if (nextByte == '\0') {
            numOfZero++;
            if (numOfZero == 1)
                this.userName = popString();
            if (numOfZero == 2) {
                this.content = popString();
                initiateValues();
                return new PMRequest(myUserName,userName, content);
            }
        }
        return null;

    }

    private Message stat (byte nextByte){
        if (nextByte == '\0') {
            this.userName = popString();
            initiateValues();
            return new StatsRequest(this.myUserName,userName);
        }
        return null;

    }


    private void pushByte(byte nextByte){
        if(len>=bytes.length)
            bytes= Arrays.copyOf(bytes,len*2);
        bytes[len++]=nextByte;

    }

    private String popString(){
        String result=new String(bytes,start,len-start-1, StandardCharsets.UTF_8);
        start=len;
        return result;
    }

    @Override
    public byte[] encode(Message message) {
        int opcode=((Response)message).getOpcode();
        String content = ((Response)message).getContent();
        short opcodeOfAMessage;
        byte[] opcodeByte;
        byte[] contentByte;
        byte[] opcodeOfAMessagetOByte;
        byte[] numOfUsersByte;
        byte[] outputByte = null;


        switch (opcode){
            case 9://notification
                opcodeByte = shortToBytes((short)9);
                contentByte = content.getBytes();
                outputByte = mergesArray(opcodeByte, contentByte);
                break;
            case 10://ACK
                opcodeByte = shortToBytes((short)10);
                opcodeOfAMessage = Short.valueOf(content.substring(0,1));
                 if(opcodeOfAMessage==2)
                     this.myUserName=this.myUserNameForLogin;
                opcodeOfAMessagetOByte = shortToBytes(opcodeOfAMessage);
                outputByte = mergesArray(opcodeByte, opcodeOfAMessagetOByte);
                content = content.substring(1);

                switch (opcodeOfAMessage) {
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 4://FOLLOW
                        String numOfUsers = ((Response) message).getNumOfUsers();
                        numOfUsersByte = shortToBytes(Short.valueOf(numOfUsers));
                        outputByte = mergesArray(outputByte, numOfUsersByte);
                        content = content.substring(1);
                        contentByte = content.getBytes();
                        outputByte = mergesArray(outputByte, contentByte);
                        break;


                    case 7: //User List
                        numOfUsers = ((Response) message).getNumOfUsers();
                        numOfUsersByte = shortToBytes(Short.valueOf(numOfUsers));
                        outputByte = mergesArray(outputByte, numOfUsersByte);
                        contentByte = content.getBytes();
                        outputByte = mergesArray(outputByte, contentByte);
                        break;

                    case 8: // Stat
                        byte[] NumPosts = shortToBytes(Short.valueOf(((Response) message).getStat()[0]));
                        byte[] NumFollowers = shortToBytes(Short.valueOf(((Response) message).getStat()[1]));
                        byte[] NumFollowing = shortToBytes(Short.valueOf(((Response) message).getStat()[2]));
                        outputByte = mergesArray(outputByte, NumPosts);
                        outputByte = mergesArray(outputByte, NumFollowers);
                        outputByte = mergesArray(outputByte, NumFollowing);
                        break;
                }
                break;

            case 11://Error
                opcodeByte = shortToBytes((short)11);
                opcodeOfAMessage = Short.valueOf(content);
                contentByte = shortToBytes(opcodeOfAMessage);
                outputByte = mergesArray(opcodeByte,contentByte);
                break;

        }
        return outputByte;


    }


    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }
    private byte[] mergesArray (byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        int i;
        for (i = 0 ; i < a.length; i ++){
            result[i] = a[i];
        }
        for (int j = 0; j < b.length; j++) {
            result[i+j] = b[j];
        }
        return result;
    }

    private void initiateValues(){
        numOfZero = 0;
        start=2;
        len=0;
        opcode=0;
        numOfUsers=-1;
    }
}
