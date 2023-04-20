package it.polimi.ingsw.networking.RMI;

import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.ConnectionException;
import it.polimi.ingsw.networking.DisconnectedException;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

/**
 * {@link Connection Connection} class that represents one side of an RMI pair of connections.
 *
 * @author Michele Miotti
 */
public class RMIConnection extends UnicastRemoteObject implements Connection, StringRemote {
    /**
     * Queue of all received messages so far.
     */
    private final Queue<String> messages = new LinkedList<>();

    /**
     * This is a stub for the other connection. This object will be called in the send method,
     * as if it were local. All errors will be thrown out through the DisconnectedException.
     */
    private final StringRemote otherConnection;

    /**
     * How much time passes between two keep-alive pings, expressed in milliseconds.
     */
    private static final int PERIOD = 2500;

    /**
     * Used to keep track of connection state. Methods "send" and "receive" can only work if this
     * boolean is false.
     */
    private boolean disconnected = false;

    /**
     * Lock needed to protect portions of object state that need to be modified by threads, such as
     * the "disconnected" boolean, or the messages queue.
     */
    private final Object lock = new Object();

    /**
     * This constructor creates a connection with some {@link it.polimi.ingsw.networking.ConnectionAcceptor acceptor}.
     * This acceptor will then pair this object with another {@link Connection connection}.
     * This constructor should be used client-side.
     *
     * @param address is the address of the server's host.
     * @param port is the port used by {@link it.polimi.ingsw.networking.ConnectionAcceptor the server} for RMI communication.
     * @throws ConnectionException will be thrown if a failure occurs in the process of creating a new Connection.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     */
    public RMIConnection(String address, int port) throws ConnectionException, RemoteException {
        try {
            // get the server object, and ask to reserve a new name for the couple.
            Registry registry = LocateRegistry.getRegistry(address, port);
            NameProvidingRemote server = (NameProvidingRemote) registry.lookup("SERVER");
            String name = server.getNewCoupleName();

            // bind this to registry, and tell the server to create its own connection.
            registry.bind(name + "CLIENT", this);
            server.createRemoteConnection(port, name);

            // get the newly created server-side object.
            otherConnection = (StringRemote) registry.lookup(name + "SERVER");

        } catch (Exception exception) {
            throw new ConnectionException();
        }

        heartbeat();
    }

    /**
     * This constructor does NOT request names to an {@link it.polimi.ingsw.networking.ConnectionAcceptor acceptor},
     * and should only be constructed BY an acceptor to generate new connections, while already knowing the target's name.
     * This method should be called server-side.
     *
     * @param port is the port used by {@link it.polimi.ingsw.networking.ConnectionAcceptor the server} for RMI communication.
     * @param connectionName is the name of the Connection pair that needs to be completed with the server-side connection.
     * @throws ConnectionException will be thrown if a failure occurs in the process of creating a new Connection.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     */
    public RMIConnection(int port, String connectionName) throws ConnectionException, RemoteException {
        try {
            // bind this to registry, assuming this method is called server side.
            Registry registry = LocateRegistry.getRegistry(port);
            registry.bind(connectionName + "SERVER", this);

            // get the client-side connection.
            otherConnection = (StringRemote) registry.lookup(connectionName + "CLIENT");

        } catch (Exception exception) {
            throw new ConnectionException();
        }

        heartbeat();
    }

    @Override
    public void send(String string) throws DisconnectedException {
        synchronized (lock) {
            if (disconnected) {
                // we can't send messages if we're disconnected.
                throw new DisconnectedException();
            }

            try {
                // simply send the message to the remote object.
                otherConnection.handleMessage(string);
            } catch (Exception exception) {
                throw new DisconnectedException();
            }
        }
    }

    @Override
    public String receive() throws DisconnectedException {
        synchronized (lock) {
            if (disconnected) {
                // we can't receive messages if we're disconnected.
                throw new DisconnectedException();
            }
            // keep checking if we've received a string.
            while (messages.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }

                if (disconnected) {
                    // we can't receive messages if we're disconnected.
                    throw new DisconnectedException();
                }
            }
            // return the next item from the queue.
            return messages.poll();
        }
    }

    @Override
    public void disconnect() {
        synchronized (lock) {
            disconnected = true;
            lock.notifyAll();
        }
    }

    /**
     * Starts a timer that pings the target, to check for connectivity.
     */
    private void heartbeat() {
        Timer timer = new Timer();

        // create a timer task to ping the target
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                synchronized (lock) {
                    // cancel timer if already disconnected
                    if (disconnected) {
                        timer.cancel();
                    }
                    try {
                        // disconnect if ping object is disconnected
                        if (!otherConnection.ping()) {
                            disconnected = true;
                            timer.cancel();
                        }
                    } catch (RemoteException exception) {
                        // disconnect if ping fails
                        disconnected = true;
                        lock.notifyAll();
                        timer.cancel();
                    }
                }
            }
        };

        // run timer
        timer.schedule(task, 0, PERIOD);
    }

    @Override
    public void handleMessage(String message) throws RemoteException {
        synchronized (lock) {
            messages.add(message);
            lock.notifyAll();
        }
    }

    @Override
    public boolean ping() throws RemoteException {
        synchronized (lock) { return !disconnected; }
    }
}
