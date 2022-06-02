
1)
1.1)TPC: mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.TPCMain" -Dexec.args="7777"
    Reactor: mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.ReactorMain" -Dexec.args="7777 5"

1.2) REGISTER amit madmoni 05-01-1997
     LOGIN amit madmoni 1
     LOGOUT
     FOLLOW 0 roey
     FOLLOW 1 roey
     PM roey hello
     POST hello @roey there
     LOGSTAT
     STAT roey|danielle
     BLOCK roey

2) Filtered set of words is located in BidiProtocol.
