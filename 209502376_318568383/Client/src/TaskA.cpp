
#include <iomanip>
#include "../include/TaskA.h"
class lock;
TaskA::TaskA(ConnectionHandler* c,std::mutex* m, std::condition_variable* v): ch(), mutex(),v(){
    this->ch=c;
    this->mutex=m;
    this->v=v;
}

void TaskA::run() {
    while (!shouldTerminate) {
        const short inputBufferSize = 1024;
        char buffer[inputBufferSize];
        std::vector<std::string> command;

        std::cin.getline(buffer, inputBufferSize);
        std::string inputLine(buffer);
        std::istringstream stringStream(inputLine);

        for (std::string currentString; stringStream >> currentString;)
            command.push_back(currentString);

        std::string commandName = command[0];

        if (commandName == "REGISTER") {
            convertSend(1);
            ch->sendLine(command[1]);
            ch->sendLine(command[2]);
            ch->sendLine(command[3]);
        } else if (commandName == "LOGIN") {
            convertSend(2);
            ch->sendLine(command[1]);
            ch->sendLine(command[2]);
            ch->sendLine(command[3]);
        } else if (commandName == "LOGOUT") {
            convertSend(3);
            std::unique_lock<std::mutex> lock(*mutex);
            v->wait(lock);
            break;
        } else if (commandName == "FOLLOW") {
            if (command.size() > 2) {
                convertSend(4);
                ch->sendLine(command[1]);
                ch->sendLine(command[2]);
            }
        } else if (commandName == "POST") {
            std::string postContent;
            if (command.size() > 1)
                postContent = command[1];
            for (unsigned int i = 2; i < command.size(); i++)
                postContent += " " + command[i];
            convertSend(5);
            ch->sendLine(postContent);

        } else if (commandName == "PM") {
            if (command.size() > 1) {
                std::string pmContent;
                if (command.size() > 2)
                    pmContent = command[2];
                for (unsigned int i = 3; i < command.size(); i++) {
                    pmContent += " " + command[i];
                }
                convertSend(6);
                ch->sendLine(command[1]);
                ch->sendLine(pmContent);
                auto t = std::time(nullptr);
                auto tm = *std::localtime(&t);
                std::ostringstream oss;
                oss << std::put_time(&tm, "%d-%m-%Y %H-%M-%S");
                auto str = oss.str();
                ch->sendLine(pmContent);
                //ch->sendLine(str);
            }
        } else if (commandName == "LOGSTAT") {
            convertSend(7);
        }

        else if (commandName == "STAT") {
            if (command.size() == 2) {
                convertSend(8);
                ch->sendLine(command[1]);
            }
        }
        else if (commandName == "BLOCK"){
            convertSend(12);
            ch->sendLine(command[1]);
        }
    }
}

void TaskA::convertSend(short number) {
    char ByteArray[2];
    ByteArray[0] = static_cast<char>((number >> 8) & 0xFF);
    ByteArray[1] = static_cast<char>(number & 0xFF);
    ch->sendBytes(ByteArray,2);
}

void TaskA::terminate() {shouldTerminate = true;}





