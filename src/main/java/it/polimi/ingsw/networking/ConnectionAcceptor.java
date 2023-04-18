package it.polimi.ingsw.networking;

import it.polimi.ingsw.networking.RMI.NameProvidingRemote;
import it.polimi.ingsw.networking.RMI.RMIConnection;

import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

/**
 * This object will keep waiting for {@link Connection connections} to send data.
 * New {@link Connection connections} will then be created to communicate back.
 *
 * @author ...
 */
public class ConnectionAcceptor implements NameProvidingRemote {
    /**
     * This constructor needs both ports, because this particular object will be used
     * server-side. These ports will listen for communications from {@link Connection connections}.
     *
     * @param TCPPort is the port used to listen for Socket communications.
     * @param RMIPort is the port used to listen for RMI communications.
     */
    public ConnectionAcceptor(int TCPPort, int RMIPort) {
        this.TCPPort = TCPPort;
        this.RMIPort = RMIPort;

        // whatever is contained in RMIConnection is as good as "null".
        cacheValid = false;
    }

    /**
     * Port used for TCP connection.
     */
    private final int TCPPort;

    /**
     * Port used for RMI connection.
     */
    private final int RMIPort;

    /**
     * Indicates the index of the most recent RMI connection couple's name.
     */
    private int lastRMIConnectionIndex;

    /**
     * Last cached RMIConnection, received through the createRemoteConnection method.
     */
    private RMIConnection RMIConnection;

    /**
     * Whenever this bool is false, the content of "RMIConnection" is treated as if it were "null".
     */
    private boolean cacheValid;

    /**
     * This method will simply wait for a connection, and return a connection object to connect
     * back to that caller
     */
    public Connection accept() {
        // keep checking if we've received a string
        while (!cacheValid) {
            try {
                // wait a little bit after each check
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException exception) {
                System.out.println("interrupted while sleeping");
            }
        }

        cacheValid = false;

        // this is technically rep exposure, but the cacheValid bool
        // should keep things stable. feel free to suggest better solutions.
        return RMIConnection;
    }

    @Override
    public String getNewCoupleName() throws RemoteException {
        // increment index and decorate it.
        return "CONNECTION" + ++lastRMIConnectionIndex;
    }

    @Override
    public void createRemoteConnection(String name) throws RemoteException {
        // override the current stashed RMIConnection
        this.RMIConnection = new RMIConnection(null, RMIPort, name);
        cacheValid = true;
    }
}
