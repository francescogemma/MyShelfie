package it.polimi.ingsw.networking;

import it.polimi.ingsw.networking.RMI.NameProvidingRemote;
import it.polimi.ingsw.networking.RMI.RMIConnection;
import it.polimi.ingsw.networking.TCP.TCPConnection;

import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This object will keep waiting for {@link Connection connections} to request a pair element.
 * New {@link Connection connections} will be created to communicate back.
 *
 * @author Francesco Gemma
 * @author Michele Miotti
 */
public class ConnectionAcceptor extends UnicastRemoteObject implements NameProvidingRemote {
    /**
     * Socket object used to implement TCP support.
     */
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
    private static int lastRMIConnectionIndex; // TODO: is it realistically possible to overflow indices?

    /**
     * A queue of Connections, received through the createRemoteConnection method and through a thread
     * created in the "receive" method.
     */
    private final Queue<Connection> connectionQueue = new LinkedList<>();

    /**
     * Lock mainly needed to protect the connectionQueue from concurrent access.
     */
    private final Object acceptLock = new Object();

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
            // export to server's localhost.
            Registry registry = LocateRegistry.createRegistry(RMIPort);
            registry.rebind("SERVER", this);

            // initialize the server socket.
            serverSocket = new ServerSocket(TCPPort);

        } catch (Exception exception) {
            throw new ConnectionException();
        }
    }

    /**
     * This method will simply wait for a connection, and return a connection object to connect
     * back to that caller
     */
    public Connection accept() {
        // this thread will concur with the createRemoteConnection method.
        Thread tcpThread = new Thread(() -> {
            try {
                // create a tcpConnection and add it to the queue.
                TCPConnection tcpConnection = new TCPConnection(serverSocket.accept());
                synchronized (acceptLock) {
                    connectionQueue.add(tcpConnection);
                    acceptLock.notifyAll();
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        // finally, start the thread.
        tcpThread.start();

        synchronized (acceptLock) {
            // make all threads wait for a new connection.
            while (connectionQueue.isEmpty()) {
                try {
                    acceptLock.wait();

                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }

            // once the queue is not empty, return the oldest object.
            return connectionQueue.poll();
        }
    }

    @Override
    public void createRemoteConnection(int port, String coupleName) throws RemoteException {
        synchronized (acceptLock) {
            try {
                // create the connection and add it to the queue.
                connectionQueue.add(new RMIConnection(port, coupleName));
                acceptLock.notifyAll();

            } catch (ConnectionException connectionException) {
                throw new RemoteException();
            }
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
