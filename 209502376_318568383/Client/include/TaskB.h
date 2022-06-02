//
// Created by spl211 on 29/12/2021.
//

#ifndef BOOST_ECHO_CLIENT_TASKB_H
#define BOOST_ECHO_CLIENT_TASKB_H

#include <thread>
#include <iostream>
#include <mutex>
#include "connectionHandler.h"
#include "TaskA.h"

class TaskB{
public:
    TaskB(ConnectionHandler* c,std::mutex* m, std::condition_variable* v);
    void run();
    short bytesToShort (char* bytesArr);
    void terminate();

private:
    ConnectionHandler* ch;
    std::mutex* mutex;
    std::condition_variable* v ;
    bool shouldTerminate= false;
};

#endif //BOOST_ECHO_CLIENT_TASKB_H
