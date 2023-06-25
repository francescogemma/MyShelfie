package it.polimi.ingsw.networking.RMI;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

/**
 * This class is a custom socket factory for RMI connections.
 * It is used to set a timeout on the socket connection.
 */
public class TimeoutSocketFactory extends RMISocketFactory {
    @Override
    public Socket createSocket(String host, int port) throws IOException {
        Socket socket = new Socket();

        socket.setSoTimeout(5000);
        socket.setSoLinger(false, 0);
        socket.connect(new InetSocketAddress(host, port), 5000);

        return socket;
    }

    @Override
    public ServerSocket createServerSocket(int port) throws IOException {
        return new ServerSocket(port);
    }
}
