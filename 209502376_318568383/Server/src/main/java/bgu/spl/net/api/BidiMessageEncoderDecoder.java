package bgu.spl.net.api;

import bgu.spl.net.api.Messages.*;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;

public class BidiMessageEncoderDecoder implements MessageEncoderDecoder<MessageImpl>{

    private byte[] r = new byte[2], r1 = new byte[1 << 10], r2 = new byte[1 << 10];
    private int word = 0, Index1 = 0, Index2 = 0;
    private short currOp = 0;
    private short followOrUnfollow;
    private String firstWord, secondWord, thirdWord;
    private final byte[] zero = {(byte) '\0'}; // keep a singleton of the zero byte
    private final byte[] one = {(byte) '1'}; // keep a singleton of the one byte
    private LinkedList<String> taggedList = new LinkedList<>();


    public MessageImpl decodeNextByte(byte nextByte) {
        MessageImpl output = null;
        if (currOp == 0) {    //if we didn't finish reading the opcode
            r[word++] = nextByte;
            if (word == 2){         //if we finished reading the opcode
                currOp = bts();     //convert the bytes array to short representing the opcode
                if (currOp == 3) { // LOGOUT
                    output = new Logout();
                    currOp = 0;
                }
                else if (currOp == 7) { // LOGSTAT
                    output = new Logstat();
                    currOp = 0;
                }
                word = 0;
            }
        }
        //             register              login                  PM
        else if (currOp == 1 || currOp == 2 || currOp == 6) {
            if (word == 0) { // we didn't finish reading the first string
                if (nextByte != 0)
                    add(nextByte, 1);
                else { // we finished reading the first string
                    firstWord = extract(1);    //the first string is the userName
                    word++;
                }
            } else if (word == 1) {       //we read the first string but didn't read the second
                if (nextByte != 0)
                    add(nextByte, 1);
                else { // we finished reading the second string
                    secondWord = extract(1);
                    word++;
                }
            } else if (word == 2) {    //we read the second string but didn't read the third
                if (nextByte != 0)
                    add(nextByte, 1);
                else { // we finished reading the third string
                    thirdWord = extract(1);
                    if (currOp == 1) {       //if it's REGISTER
                        output = new Register(firstWord, secondWord, thirdWord);
                    } else if (currOp == 2)     //if it's LOGIN
                        output = new Login(firstWord, secondWord, thirdWord);
                    else {                          //if it's PM
                        System.out.println(thirdWord);
                        output = new PM(firstWord, secondWord, thirdWord);
                    }
                    firstWord = "";
                    secondWord = "";
                    thirdWord = "";
                    word = 0;
                    currOp = 0;
                }
            }
        }
        else if (currOp == 4){        // FollowUnfollow
            if (word == 0) {   // means we haven't read the followOrUnfollow field yet
                short temp = (short) nextByte;
                int temp1 = temp;
                temp1 = temp1 - 48;
                followOrUnfollow=(short)temp1;
                word++;
            }
            else if(word == 1)
                word++;
            else if (word == 2) {
                if (nextByte != 0) // means we haven't read the current user name yet
                    add(nextByte,1);
                else { // means we've finished reading the current user name
                    firstWord = extract(1);
                    output = new FollowUnfollow(followOrUnfollow, firstWord);
                    word = 0;
                    currOp = 0;
                    firstWord = "";
                }
            }
        }

        else if (currOp == 5){ // Post
            if (nextByte != 0) { // means we haven't finished reading the content
                if (word == 0) { // means we're not reading a username
                    add(nextByte,1);
                    if (nextByte == 64) // means we're reading the '@' char
                        word++;
                }
                else { // means we're reading a username
                    if (nextByte != 32) {
                        add(nextByte,1);
                        add(nextByte, 2);
                    }
                    else {        // means we're reading the spacebar char
                        add(nextByte,1);
                        firstWord = extract(2);

                        if (!taggedList.contains(firstWord))
                            taggedList.add(firstWord);

                        firstWord = "";
                        word = 0;
                    }
                }
            }
            else { // we've reached the end of the content
                if (word > 0){ // if we finished the line when a user was read
                    firstWord = extract(2);
                    if (!taggedList.contains(firstWord))
                        taggedList.add(firstWord);
                    firstWord = "";
                    word = 0;
                }
                if (taggedList.isEmpty())
                    output = new Post(extract(1));
                else
                    output = new Post(extract(1), taggedList);
                currOp = 0;
                taggedList = new LinkedList<>();
            }
        }

        else if (currOp == 8){ // Stat
            if (nextByte != 0) { // means we haven't read the current user name yet
                if(nextByte!=124){        //if it is not '|'
                    add(nextByte, 1);
                }
                else{
                    firstWord = extract(1);
                    if (!taggedList.contains(firstWord))
                        taggedList.add(firstWord);
                    firstWord = "";
                }
            }
            else{
                firstWord = extract(1);
                if (!taggedList.contains(firstWord))
                    taggedList.add(firstWord);
                firstWord = "";
                if(!taggedList.isEmpty()){
                    output = new Stat(taggedList);
                }
                currOp = 0;
                taggedList =  new LinkedList<>();
            }
        }
        else if (currOp == 12){ // block
            if (nextByte != 0) // means we haven't read the current user name yet
                add(nextByte,1);
            else { // means we've finished reading the current user name
                firstWord = extract(1);
                output = new Block(firstWord);
                firstWord = "";
                currOp = 0;
            }
        }
        return output; // returns null if not assigned
    }


    public byte[] encode(MessageImpl message) {
        byte[] output = null;
        short opCode = message.getOpCode();
        byte[] OpCodeArray = shortToBytes(opCode);   //bytes array just for the opcode
        if (opCode == 9) { // Notification
            Notification currAckMsg = (Notification) message;
            byte[] type = {(byte) currAckMsg.getType()};
            output = concatenation(OpCodeArray, type, currAckMsg.getUser().getBytes(),
                    zero, currAckMsg.getContent().getBytes(), zero);
        }
        else if (opCode == 10){ // ackmsg
            ackMsg currAckMsg = (ackMsg) message;
            short msgOpCope = currAckMsg.getMessageOpCope();
             //    follow/unfollow       block
            if (msgOpCope == 4 || msgOpCope == 12) {
                if (msgOpCope == 4 && currAckMsg.getFollowORunfollow() == 0)
                    output = concatenation(OpCodeArray, shortToBytes(msgOpCope),
                            zero, currAckMsg.getUsername().getBytes(), zero);
                else if (msgOpCope == 4 && currAckMsg.getFollowORunfollow() == 1)
                    output = concatenation(OpCodeArray, shortToBytes(msgOpCope),
                            one, currAckMsg.getUsername().getBytes(), zero);
                    else{
                    output = concatenation(OpCodeArray, shortToBytes(msgOpCope),
                            currAckMsg.getUsername().getBytes());
                }
            }
            else if (msgOpCope == 7 || msgOpCope == 8){    //logstat or stat

                output = concatenation(OpCodeArray, shortToBytes(msgOpCope)
                        , shortToBytes(currAckMsg.getAge()), shortToBytes(currAckMsg.getNumOfPosts()),
                        shortToBytes(currAckMsg.getNumOfFollowers()), shortToBytes(currAckMsg.getNumOfFollowing()));
            }
            else         //used for REGISTER,LOGIN,LOGOUT,POST,PM
                output = concatenation(OpCodeArray, shortToBytes((msgOpCope)));

        }
        else if (opCode == 11) // ErrorMessage
            output = concatenation(OpCodeArray, shortToBytes(((Error_spl) message).getMessageOpCope()));


        return output;
    }

    private short bts() {
        short output = (short)((r[0] & 0xff) << 8);
        output += (short)(r[1] & 0xff);
        r = new byte[2];
        return output;
    }

    private void add(byte nextByte, int index) {
        if (index == 1) {
            if (Index1 >= r1.length)
                r1 = Arrays.copyOf(r1, Index1 * 2);
            r1[Index1++] = nextByte;
        }
        else{
            if (Index2 >= r2.length)
                r2 = Arrays.copyOf(r2, Index2 * 2);
            r2[Index2++] = nextByte;
        }
    }

    private byte[] shortToBytes(short shortInput) {
        byte[] outputByteArray = new byte[2];
        outputByteArray[0] = (byte)((shortInput >> 8) & 0xFF);
        outputByteArray[1] = (byte)(shortInput & 0xFF);
        return outputByteArray;
    }

    private String extract(int index) {
        String output;
        if (index == 1) {
            output = new String(r1, 0, Index1, StandardCharsets.UTF_8);
            Index1 = 0;
            r1 = new byte[1 << 10];
        }
        else {
            output = new String(r2, 0, Index2, StandardCharsets.UTF_8);
            Index2 = 0;
            r2 = new byte[1 << 10];
        }
        return output;
    }

    private byte[] concatenation(byte[]... byteArrays){
        int length = 0;
        // record the length of the output array
        for (byte[] curr : byteArrays)
            length += curr.length;
        byte[] output = new byte[length]; // create the output array with the desired size
        int position = 0; // an index that keeps track of the array position
        for (byte[] curr : byteArrays) {
            System.arraycopy(curr, 0, output, position, curr.length);
            position += curr.length;
        }
        return output;
    }
}
