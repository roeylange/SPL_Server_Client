

#include <mutex>
#include <condition_variable>
#include "connectionHandler.h"
#include "TaskA.h"
#include "TaskB.h"

int main (int argc, char *argv[]){
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

//    std::string host = "127.0.0.1";
//    short port = 7777;

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    std:: mutex logMutex;
    std:: condition_variable conditionVariable;
    TaskA a(&connectionHandler,&logMutex,&conditionVariable);
    TaskB b(&connectionHandler,&logMutex,&conditionVariable);
    std::thread t1(&TaskA::run,&a);
    std::thread t2(&TaskB::run,&b);
    t2.join();
    a.terminate();
    conditionVariable.notify_all();
    t1.join();
    connectionHandler.close();
}

