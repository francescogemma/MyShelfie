package it.polimi.ingsw.networking;

import it.polimi.ingsw.networking.RMI.RMIConnection;
import it.polimi.ingsw.networking.RMI.RemoteLinkedList;
import it.polimi.ingsw.networking.RMI.RemoteServer;
import it.polimi.ingsw.networking.RMI.TimeoutSocketFactory;
import it.polimi.ingsw.networking.TCP.TCPConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This object will keep waiting for {@link Connection connections} to request a pair element.
 * New {@link Connection connections} will be created to communicate back. This object also acts as a server,
 * and has a remotely invokable method.
 *
 * @author Francesco Gemma
 * @author Michele Miotti
 */
public class ConnectionAcceptor extends UnicastRemoteObject implements RemoteServer {
    /**
     * Socket object used to implement TCP support.
     */
    private final ServerSocket serverSocket;

    /**
     * Port used for RMI communication
     */
    private final int RMIPort;

    /**
     * Lock used to handle multiple threads requesting names concurrently.
     * It is static because many ConnectionAcceptors may be instantiated, and they all need to share
     * the same lastRMIConnectionIndex.
     */
    private static final Object lock = new Object();

    /**
     * Needed for RMI communication.
     * Indicates the index of the next index that will be assigned as a name to a new remote queue pair.
     * It is static because even if many ConnectionAcceptors may be instantiated,
     * the values contained in the registry are the same for all of them.
     */
    private static int nextBoundIndex;

    /**
     * A queue of Connections, received through the createRemoteConnection method and through a thread
     * created in the "receive" method.
     */
    private final Queue<Connection> connectionQueue = new LinkedList<>();

    /**
     * Pointer to this server's registry.
     */
    private final Registry registry;

    /**
     * This constructor needs both ports, because this particular object will be used
     * server-side, so both TCP and RMI must be supported.
     *
     * @param TCPPort the port that TCP will listen through.
     * @param RMIPort the port that RMI will listen through.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     * @throws ConnectionException will be thrown if a failure occurs in the process of creating a new Connection.
     */

    public ConnectionAcceptor(int TCPPort, int RMIPort) throws RemoteException, ConnectionException {
        this.RMIPort = RMIPort;

        try {
            registry = LocateRegistry.createRegistry(RMIPort);
            registry.bind("SERVER", this);
            serverSocket = new ServerSocket(TCPPort);
        } catch (Exception exception) {
            throw new ConnectionException();
        }
    }

    /**
     * Static method to initialize ConnectionAcceptor. Sets the RMI variable "hostname" to the provided string.
     * @param hostName will be assigned to the "hostname" RMI variable.
     */
    public static void initialize(String hostName) {
        System.setProperty("java.rmi.server.hostname", hostName);

        try {
            RMISocketFactory.setSocketFactory(new TimeoutSocketFactory());
        } catch (IOException e) {
            throw new IllegalStateException("Error while setting RMI socket factory");
        }
    }

    /**
     * This method will simply wait for a connection, and return a connection object to connect
     * back to that caller
     *
     * @return {@link Connection} object
     */
    public Connection accept() {
        // this thread will concur with the createRemoteConnection method.
        Thread tcpThread = new Thread(() -> {
            try {
                // create a tcpConnection and add it to the queue.
                TCPConnection tcpConnection = new TCPConnection(serverSocket.accept());
                synchronized (lock) {
                    connectionQueue.add(tcpConnection);
                    lock.notifyAll();
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        // finally, start the thread.
        tcpThread.start();

        synchronized (lock) {
            while (connectionQueue.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }

            return connectionQueue.poll();
        }
    }

    @Override
    public String getBoundName() throws RemoteException {
        synchronized (lock) {
            String boundName = "QUEUE" + nextBoundIndex++;

            try {
                registry.bind("ADD_" + boundName, new RemoteLinkedList());
                registry.bind("POLL_" + boundName, new RemoteLinkedList());
            } catch (AlreadyBoundException alreadyBoundException) {
                throw new RuntimeException("queue already bound");
            }

            try {
                RMIConnection rmiConnection = new RMIConnection("localhost", RMIPort, boundName);
                connectionQueue.add(rmiConnection);
                lock.notifyAll();
            } catch (ServerNotFoundException serverNotFoundException) {
                serverNotFoundException.printStackTrace();
            }

            return boundName;
        }
    }
}
