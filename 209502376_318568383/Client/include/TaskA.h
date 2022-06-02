#ifndef BOOST_ECHO_CLIENT_TASKA_H
#define BOOST_ECHO_CLIENT_TASKA_H

#include <thread>
#include <iostream>
#include <mutex>
#include <condition_variable>
#include "connectionHandler.h"

class TaskA{
public:
    TaskA(ConnectionHandler* currentConnectionHandler, std::mutex* mutex, std::condition_variable* conditionVariable);
    TaskA();
    void run();
    void convertSend(short number);
    void terminate();

private:
    ConnectionHandler* ch;
    std::mutex* mutex;
    std::condition_variable* v;
    bool shouldTerminate= false ;
};

#endif //BOOST_ECHO_CLIENT_TASKA_H


