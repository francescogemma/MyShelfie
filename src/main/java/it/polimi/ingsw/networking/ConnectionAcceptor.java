package it.polimi.ingsw.networking;

import it.polimi.ingsw.networking.RMI.NameProvidingRemote;
import it.polimi.ingsw.networking.RMI.RMIConnection;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

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
     * Lock used to handle multiple threads requesting names concurrently.
     * It is static because many ConnectionAcceptors may be instantiated, and they all need to share
     * the same lastRMIConnectionIndex.
     */
    private static final Object getNameLock = new Object();

    /**
     * Indicates the index of the most recent RMI connection couple's name.
     * It is static because even if many ConnectionAcceptors may be instantiated,
     * the values contained in the registry are the same for all of them.
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
    public synchronized Connection accept() {
        // make all threads wait for a new connection
        while (!cacheValid) {
            try {
                wait();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
        // invalidate cache and notify all producers
        cacheValid = false;
        notifyAll();

        // this is technically rep exposure, but the cacheValid bool
        // should keep things stable. feel free to suggest better solutions.
        RMIConnection.heartbeat();
        return RMIConnection;
    }

    @Override
    public synchronized void createRemoteConnection(String coupleName) throws RemoteException, ConnectionException {
        // make all threads wait for the cache to be empty
        while (cacheValid) {
            try {
                wait();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
        // override the current stashed RMIConnection
        this.RMIConnection = new RMIConnection(1099, coupleName);

        // validate cache and notify all consumers
        cacheValid = true;
        notifyAll();
    }

    @Override
    public String getNewCoupleName() throws RemoteException {
        synchronized (getNameLock) {
            // increment index and decorate it.
            return "CONN" + ++lastRMIConnectionIndex;
        }
    }
}
