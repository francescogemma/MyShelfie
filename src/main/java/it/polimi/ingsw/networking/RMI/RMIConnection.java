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
     * @param name will identify this connection and the newly created server-side connection in the registry.
     *             It will do so by appending the suffix "Client" and "Server" to this parameter.
     */
    public RMIConnection(String name) {
        // start, assuming connection is working correctly
        disconnected = false;

        try {
            // get the registry with no arguments.
            // which means we're using the local host address.
            Registry registry = LocateRegistry.getRegistry();

            // create and export our stringContainer
            stringContainer = new RMIStringContainer();
            StringRemote stub = (StringRemote) UnicastRemoteObject.exportObject(
                    stringContainer, RMIStringContainer.STANDARD_PORT
            );

            // bind it to the registry
            registry.bind(name, stub);

            // get the server object, to get another connection to pair up with.
            NameProvidingRemote server = (NameProvidingRemote) registry.lookup("Server");

            // use the information provided by the server to pair with the other connection
            String otherConnectionName = server.getRemoteConnectionName(name);
            remoteContainer = (RMIStringContainer) registry.lookup(otherConnectionName);
        } catch (Exception exception) {
            // instead of crashing the program, count this error as a disconnection.
            disconnected = true;
        }

        heartbeat();
    }

    /**
     * This constructor does NOT request names to an {@link it.polimi.ingsw.networking.ConnectionAcceptor acceptor},
     * and should only be used BY an acceptor to generate new connections, while already knowing the target's name.
     *
     * @param name this connection's name
     * @param targetName the name of the target connection.
     */
    public RMIConnection(String name, String targetName) {
        // start, assuming connection is working correctly
        disconnected = false;

        try {
            // get the registry with no arguments.
            // which means we're using the local host address.
            Registry registry = LocateRegistry.getRegistry();

            // create and export our stringContainer
            stringContainer = new RMIStringContainer();
            StringRemote stub = (StringRemote) UnicastRemoteObject.exportObject(
                    stringContainer, RMIStringContainer.STANDARD_PORT
            );

            // bind it to the registry
            registry.bind(name, stub);

            remoteContainer = (RMIStringContainer) registry.lookup(targetName);
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
