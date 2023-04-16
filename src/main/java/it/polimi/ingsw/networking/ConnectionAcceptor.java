package it.polimi.ingsw.networking;

/**
 * This object will keep waiting for {@link Connection connections} to send data.
 * New {@link Connection connections} will then be created to communicate back.
 *
 * @author ...
 */
public class ConnectionAcceptor {
    /**
     * This constructor needs both ports, because this particular object will be used
     * server-side. These ports will listen for communications from {@link Connection connections}.
     *
     * @param socketPort is the port used to listen for Socket communications.
     * @param RMIPort is the port used to listen for RMI communications.
     */
    public ConnectionAcceptor(int socketPort, int RMIPort) {

    }

    /**
     * This method will simply wait for a connection, and return a connection object to connect
     * back to that caller
     */
    public Connection accept() {

        return null;
    }
}
