package it.polimi.ingsw.networking;

import it.polimi.ingsw.networking.RMI.NameProvidingRemote;
import it.polimi.ingsw.networking.RMI.RMIConnection;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.TimeUnit;

/**
 * This object will keep waiting for {@link Connection connections} to send data.
 * New {@link Connection connections} will then be created to communicate back.
 *
 * @author Francesco Gemma
 * @author Michele Miotti
 */
public class ConnectionAcceptor extends UnicastRemoteObject implements NameProvidingRemote {
    /**
     * This constructor needs both ports, because this particular object will be used
     * server-side, so both TCP and RMI must be supported.
     * These ports will listen for communications from {@link Connection connections}.
     *
     * @param TCPPort the port that TCP will listen through.
     * @param RMIPort the port that RMI will listen through.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     * @throws ConnectionException will be thrown if a failure occurs in the process of creating a new Connection.
     */
    public ConnectionAcceptor(int TCPPort, int RMIPort) throws RemoteException, ConnectionException {
        try {
            // export to server's localhost
            Registry registry = LocateRegistry.createRegistry(RMIPort);
            registry.bind("SERVER", this);
        } catch (Exception exception) {
            throw new ConnectionException();
        }

        // whatever is contained in RMIConnection is as good as "null".
        cacheValid = false;
    }

    /**
     * Indicates the index of the most recent RMI connection couple's name.
     */
    private static int lastRMIConnectionIndex;

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
                // TODO: don't sleep but wait
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }

        RMIConnection.heartbeat();
        cacheValid = false; // TODO: producer consumer pattern

        // this is technically rep exposure, but the cacheValid bool
        // should keep things stable. feel free to suggest better solutions.
        return RMIConnection;
    }

    @Override
    public String getNewCoupleName() throws RemoteException { // TODO: static lock here
        // increment index and decorate it.
        return "CONN" + ++lastRMIConnectionIndex;
    }

    @Override
    public void createRemoteConnection(String coupleName) throws RemoteException, ConnectionException {
        // override the current stashed RMIConnection
        this.RMIConnection = new RMIConnection(1099, coupleName);
        cacheValid = true;
    }
}
