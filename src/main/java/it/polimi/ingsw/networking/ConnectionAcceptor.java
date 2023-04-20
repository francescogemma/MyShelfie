package it.polimi.ingsw.networking;

import it.polimi.ingsw.networking.RMI.NameProvidingRemote;
import it.polimi.ingsw.networking.RMI.RMIConnection;
import it.polimi.ingsw.networking.TCP.TCPConnection;

import java.net.ServerSocket;
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
            Registry registry = LocateRegistry.createRegistry(RMIPort); // TODO: search before creating by using static variables
            registry.bind("SERVER", this);
        } catch (Exception exception) {
            throw new ConnectionException();
        }

        try {
            serverSocket = new ServerSocket(TCPPort);
        } catch (Exception exception) {
            throw new ConnectionException();
        }

        // whatever is contained in RMIConnection is as good as "null".
        rmiCacheValid = false;
        tcpCacheValid = false;
    }

    private final ServerSocket serverSocket;

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
    private RMIConnection rmiConnection;
    private TCPConnection tcpConnection; // TODO: change all other occurrences of UPPER characters

    /**
     * Whenever this bool is false, the content of "RMIConnection" is treated as if it were "null".
     */
    private boolean rmiCacheValid;
    private boolean tcpCacheValid;

    private static final Object acceptLock = new Object();

    /**
     * This method will simply wait for a connection, and return a connection object to connect
     * back to that caller
     */
    public Connection accept() {
        Thread tcpThread = new Thread(() -> {
            try {
                tcpConnection = new TCPConnection(serverSocket.accept());
                tcpCacheValid = true;
                synchronized (acceptLock) { acceptLock.notifyAll(); }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        tcpThread.start();

        synchronized (acceptLock) {
            // make all threads wait for a new connection
            while (!rmiCacheValid && !tcpCacheValid) {
                try {
                    acceptLock.wait();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }

            if (rmiCacheValid) {
                rmiCacheValid = false;
                acceptLock.notifyAll();
                return rmiConnection;
            }

            tcpCacheValid = false;
            acceptLock.notifyAll();
            return tcpConnection;
        }
    }

    @Override
    public void createRemoteConnection(String coupleName) throws RemoteException, ConnectionException {
        synchronized (acceptLock) {
            // make all threads wait for the cache to be empty
            while (rmiCacheValid) {
                try {
                    acceptLock.wait();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
            // override the current stashed RMIConnection
            this.rmiConnection = new RMIConnection(1099, coupleName);

            // validate cache and notify all consumers
            rmiCacheValid = true;
            acceptLock.notifyAll();
        }
    }

    @Override
    public String getNewCoupleName() throws RemoteException {
        synchronized (getNameLock) {
            // increment index and decorate it.
            return "CONN" + ++lastRMIConnectionIndex;
        }
    }
}
