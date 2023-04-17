package it.polimi.ingsw.networking.RMI;

import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.DisconnectedException;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * {@link Connection Connection} class that handles RMI communication.
 *
 * @author Michele Miotti
 */
public class RMIConnection implements Connection {
    /**
     * This object is an encapsulation of the RMI system. Every RMI-related aspect
     * of the RMIConnection object relies on this receiver, that extends the StringRemote interface.
     */
    private StringReceiver stringReceiver;

    /**
     * This is a stub for the remote object. This object will be called in the send method,
     * as if it were local. All errors will be thrown out through the DisconnectedException.
     */
    private StringReceiver remoteObject;

    /**
     * Used to keep track of connection state. Methods "send" and "receive" can only work if this
     * boolean is false.
     */
    private boolean disconnected;

    /**
     * This constructor creates a connection with the target by setting up the stringReceiver and
     * remoteObject attributes.
     *
     * @param name will identify this object in the registry.
     * @param targetName identifies the target object in the registry.
     */
    public RMIConnection(String name, String targetName) {
        // start, assuming connection is working correctly
        disconnected = false;

        try {
            // get the registry with no arguments.
            // which means we're using the local host address.
            Registry registry = LocateRegistry.getRegistry();

            // create and bind our stringReceiver to the registry.
            stringReceiver = new StringReceiver();
            StringReceiver stub = (StringReceiver) UnicastRemoteObject.exportObject(stringReceiver, StringReceiver.STANDARD_PORT);
            registry.bind(name, stub);

            // get the remote object, to be used with the send method.
            remoteObject = (StringReceiver) registry.lookup(targetName);
        } catch (Exception exception) {
            // instead of crashing the program, count this error as a disconnection.
            disconnected = true;
        }
    }

    @Override
    public void send(String string) throws DisconnectedException {
        if (disconnected) {
            throw new DisconnectedException();
        }

        try {
            // plainly send the message to the remote object
            remoteObject.handleMessage(string);
        } catch (Exception exception) {
            throw new DisconnectedException();
        }
    }

    @Override
    public String receive() throws DisconnectedException {
        if (disconnected) {
            throw new DisconnectedException();
        }

        // ...
        stringReceiver.getString();

        // TODO: this whole method.

        return null;
    }
}
