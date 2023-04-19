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
     * Server's registry.
     */
    private final Registry registry;

    /**
     * The name of the connection pair.
     */
    private final String name;

    /**
     * The stub for the server object.
     */
    private final NameProvidingRemote server;

    /**
     * This constructor creates a connection with some {@link it.polimi.ingsw.networking.ConnectionAcceptor acceptor}.
     * This acceptor will then pair this object with another {@link Connection connection}.
     * This constructor should be used client-side.
     *
     * @param address is the address of the server's host.
     * @param port is the port used by {@link it.polimi.ingsw.networking.ConnectionAcceptor the server} for RMI communication.
     * @throws ConnectionException will be thrown if a failure occurs in the process of creating a new Connection.
     */
    public RMIConnection(String address, int port) throws ConnectionException, RemoteException {
        try {
            // get the server object, and ask to reserve a new name for the couple.
            registry = LocateRegistry.getRegistry(address, port);
            server = (NameProvidingRemote) registry.lookup("SERVER");
            name = server.getNewCoupleName();

            // bind this to registry, and tell the server to create its own connection.
            registry.bind(name + "CLIENT", this);
            server.createRemoteConnection(name);

            // get the newly created server-side object.
            otherConnection = (StringRemote) registry.lookup(name + "SERVER");

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new ConnectionException();
        }

        // since the other object has already been created, we can start heartbeat here.
        heartbeat();
    }

    /**
     * This constructor does NOT request names to an {@link it.polimi.ingsw.networking.ConnectionAcceptor acceptor},
     * and should only be used BY an acceptor to generate new connections, while already knowing the target's name.
     * This method should be called server-side.
     *
     * @param port is the port used by {@link it.polimi.ingsw.networking.ConnectionAcceptor the server} for RMI communication.
     * @param connectionName is the name of the Connection pair that needs to be completed with the server-side connection.
     * @throws ConnectionException will be thrown if a failure occurs in the process of creating a new Connection.
     */
    public RMIConnection(int port, String connectionName) throws ConnectionException, RemoteException {
        try {
            registry = LocateRegistry.getRegistry(port);
            server = (NameProvidingRemote) registry.lookup("SERVER");
            name = connectionName;

            // bind this to registry, assuming this method is called server side.
            registry.bind(name + "SERVER", this);

            // we assume the client object is created BEFORE the server object.
            otherConnection = (StringRemote) registry.lookup(name + "CLIENT");

        } catch (Exception exception) {
            throw new ConnectionException();
        }
    }

    @Override
    public void send(String string) throws DisconnectedException {
        if (disconnected) {
            throw new DisconnectedException();
        }

        try {
            // plainly send the message to the remote object
            otherConnection.handleMessage(string);
        } catch (Exception exception) {
            throw new DisconnectedException();
        }
    }

    @Override
    public synchronized String receive() throws DisconnectedException {
        if (disconnected) {
            throw new DisconnectedException();
        }

        // keep checking if we've received a string
        while (messages.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }

        // get the next item from the queue
        String result = messages.poll();
        notifyAll();

        return result;
    }

    @Override
    public void disconnect() {
        try {
            disconnected = true;
            notifyAll();

            registry.unbind(name + "CLIENT");
            registry.unbind(name + "SERVER");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Starts a timer that pings the target, to check for connectivity.
     */
    public void heartbeat() {
        Timer timer = new Timer();

        // create a timer task that will ping the target
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    otherConnection.ping();
                } catch (RemoteException exception) {
                    disconnected = true;
                    notifyAll();

                    timer.cancel();
                }
            }
        };

        // run timer
        timer.schedule(task, 0, PERIOD);
    }

    @Override
    public synchronized void handleMessage(String message) throws RemoteException { messages.add(message); }

    @Override
    public void ping() throws RemoteException { }
}
