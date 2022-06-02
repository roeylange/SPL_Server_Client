package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.BidiMessageEncoderDecoder;
import bgu.spl.net.api.BidiProtocol;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args){
        //int port =Integer.parseInt("7777");
        int port =Integer.parseInt(args[0]);
        Server.threadPerClient(port, BidiProtocol::new, BidiMessageEncoderDecoder::new).serve();
    }
}
