package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.BidiMessageEncoderDecoder;
import bgu.spl.net.api.BidiProtocol;
import bgu.spl.net.srv.Server;

public class ReactorMain {

    public static void main(String[] args) {
//        int port = Integer.parseInt("7777");
//        int NumOfThreads = Integer.parseInt("5");
        int port = Integer.parseInt(args[0]);
        int NumOfThreads = Integer.parseInt(args[1]);
        Server.reactor(NumOfThreads,port, BidiProtocol::new, BidiMessageEncoderDecoder::new).serve();
    }

}
