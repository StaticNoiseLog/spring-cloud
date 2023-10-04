package util;

import org.h2.tools.Server;

public class H2DatabaseServer {
    public static void main(String[] args) throws Exception {
        // Start the H2 TCP server
        Server.createTcpServer("-tcpAllowOthers").start();
    }
}