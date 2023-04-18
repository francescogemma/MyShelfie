package it.polimi.ingsw.networking.RMI;

import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.DisconnectedException;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * {@link Connection Connection} class that represents an RMI connection.
 *
 * @author Michele Miotti
 */
public class RMIConnection implements Connection {
    /**
     * This object is an encapsulation of the RMI system. Every RMI-related aspect
     * of the RMIConnection object relies on this container, that extends the StringRemote interface.
     * It acts as a simple container for a string. It might contain nothing.
     */
    private RMIStringContainer stringContainer;

    /**
     * How much time passes between two pings for keep-alive.
     */
    private static final int PERIOD = 2500;

    /**
     * This is a stub for the remote string container. This object will be called in the send method,
     * as if it were local. All errors will be thrown out through the DisconnectedException.
     */
    private RMIStringContainer remoteContainer;

    /**
     * Used to keep track of connection state. Methods "send" and "receive" can only work if this
     * boolean is false.
     */
    private boolean disconnected;

    /**
     * This constructor creates a connection with some {@link it.polimi.ingsw.networking.ConnectionAcceptor acceptor}.
     * This acceptor will then pair this object with another {@link Connection connection}.
     *
     * @param port is the port that will be used for RMI (9080 suggested)
     */
    public RMIConnection(String address, int port) {
        // start, assuming connection is working correctly
        disconnected = false;

        try {
            // create and export our stringContainer
            stringContainer = new RMIStringContainer();
            StringRemote stub = (StringRemote) UnicastRemoteObject.exportObject(
                    stringContainer, port
            );

            // get the server object, and ask to reserve a new name for the couple.
            Registry registry = LocateRegistry.getRegistry(address);
            NameProvidingRemote server = (NameProvidingRemote) registry.lookup("Server");
            String connectionName = server.getNewCoupleName();

            // bind self to the registry, and tell the server to create its own connection.
            registry.bind(connectionName + "CLIENT", stub);
            server.createRemoteConnection(connectionName + "SERVER");

            // get the newly created server-side object.
            remoteContainer = (RMIStringContainer) registry.lookup(connectionName + "SERVER");

        } catch (Exception exception) {
            // instead of crashing the program, count this error as a disconnection.
            disconnected = true;
        }

        heartbeat();
    }

    /**
     * This constructor does NOT request names to an {@link it.polimi.ingsw.networking.ConnectionAcceptor acceptor},
     * and should only be used BY an acceptor to generate new connections, while already knowing the target's name.
     * This method should NOT be called client-side.
     *
     * @param connectionName the name of the connection pair.
     */
    public RMIConnection(String address, int port, String connectionName) {
        // start, assuming connection is working correctly
        disconnected = false;

        try {
            // create and export our stringContainer
            stringContainer = new RMIStringContainer();
            StringRemote stub = (StringRemote) UnicastRemoteObject.exportObject(
                    stringContainer, port
            );

            // bind it to the registry, assuming this method is called server side.
            Registry registry = LocateRegistry.getRegistry(address);
            registry.bind(connectionName + "SERVER", stub);

            // we assume the client object is created BEFORE the server object.
            remoteContainer = (RMIStringContainer) registry.lookup(connectionName + "CLIENT");
        } catch (Exception exception) {
            // instead of crashing the program, count this error as a disconnection.
            disconnected = true;
        }

        heartbeat();
    }

    @Override
    public void send(String string) throws DisconnectedException {
        if (disconnected) {
            throw new DisconnectedException();
        }

        try {
            // plainly send the message to the remote object
            remoteContainer.handleMessage(string);
        } catch (Exception exception) {
            throw new DisconnectedException();
        }
    }

    @Override
    public String receive() throws DisconnectedException {
        if (disconnected) {
            throw new DisconnectedException();
        }

        // keep checking if we've received a string
        while (!stringContainer.hasString()) {
            try {
                // wait a little bit after each check
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException exception) {
                System.out.println("interrupted while sleeping");
            }
        }

        // once we've found it, return it
        return stringContainer.getString();
    }

    /**
     * Starts a timer that pings the target, to check for connectivity.
     */
    private void heartbeat() {
        Timer timer = new Timer();

        // create a timer task that will ping the target
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    remoteContainer.ping();
                } catch (RemoteException exception) {
                    disconnected = true;
                    timer.cancel();
                }
            }
        };

        // run timer
        timer.schedule(task, 0, PERIOD);
    }
}
