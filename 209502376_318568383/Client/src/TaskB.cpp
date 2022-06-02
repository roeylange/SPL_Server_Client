
#include <condition_variable>
#include "../include/TaskB.h"
#include "TaskA.h"

TaskB::TaskB(ConnectionHandler* c,std::mutex* m, std::condition_variable* v): ch(c), mutex(m),v(v) {}

void TaskB::run() {
    while(!shouldTerminate){
        char opCodeBytes[2];
        ch->getBytes(opCodeBytes, 2);
        short opCode = bytesToShort(opCodeBytes);
        if (opCode == 9){                          // notification
            char type [1];
            ch->getBytes(type, 1);
            std::string typeString, postUser, content;
            if (type[0] == 48)                 // 0 char
                typeString = "PM";
            else
                typeString = "Public";
            ch->getFrameAscii(postUser, '\0');
            postUser = postUser.substr(0, postUser.size() - 1);
            ch->getFrameAscii(content, '\0');
            content = content.substr(0, content.size()-1);
            std::cout << "NOTIFICATION " << typeString << " " << postUser << " "
                 << content << std::endl;
        }
        else if (opCode == 10){ // Ack
            char currOpcodeB[2];
            ch->getBytes(currOpcodeB, 2); // get 2 bytes from the server for the message opCode of the ack
            short currOpCode = bytesToShort(currOpcodeB); // convert the bytes to short
            if (currOpCode == 3) {   //logout
                std::cout << "ACK " << currOpCode << std::endl; // print the desired output
                terminate(); // call to terminate the server to client thread
            }
            else if (currOpCode == 4){ // FollowUnfollow
                // record the number of users
                char followUnfollow[1];
                ch->getBytes(followUnfollow, 1);
                short tmp = bytesToShort(followUnfollow);
                std::string name;
                ch->getFrameAscii(name,'\0');
                if(tmp==0)
                    std::cout << "ACK " << currOpCode << " Follow " << name << std::endl;
                else
                    std::cout << "ACK " << currOpCode << " Unfollow " << name << std::endl;
            }
            else if (currOpCode == 8 || currOpCode == 7){   // logStat or stat
                char numBytes[2];
                ch->getBytes(numBytes,2);
                short age = bytesToShort(numBytes);
                ch->getBytes(numBytes, 2);
                short numOfPosts = bytesToShort(numBytes);
                // get the numOfFollowers
                ch->getBytes(numBytes, 2);
                short numOfFollowers = bytesToShort(numBytes);
                // get the numOfFollowing
                ch->getBytes(numBytes, 2);
                short numOfFollowing = bytesToShort(numBytes);
                std::cout << "ACK " << currOpCode << " " << age << " " << numOfPosts << " " << numOfFollowers
                     << " " << numOfFollowing << std::endl;
            }
            else
                std::cout << "ACK " << currOpCode << std::endl;
        }
        else if (opCode == 11){ // Error
            char currOpcodeB[2];
            ch->getBytes(currOpcodeB, 2);
            short currOpCode = bytesToShort(currOpcodeB);
            std::cout << "ERROR " << currOpCode << std::endl;
            if (currOpCode == 3)
                v->notify_all();
        }
    }
}

short TaskB::bytesToShort(char *bytesArr) {
    auto result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

void TaskB::terminate() {
    shouldTerminate = true;
}

