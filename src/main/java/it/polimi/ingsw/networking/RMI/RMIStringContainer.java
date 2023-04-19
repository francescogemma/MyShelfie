package it.polimi.ingsw.networking.RMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Simple object to encapsulate all RMI-related behaviour. The handleMessage method is invoked remotely, and
 * some other object needs to use the getString and hasString methods to recover the message.
 */

// TODO: make everything thread safe
public class RMIStringContainer extends UnicastRemoteObject implements StringRemote {
    /**
     * Received messages will be stored here.
     */
    private final Queue<String> messages = new LinkedList<>();

    /**
     * Object constructor.
     */
    public RMIStringContainer() throws RemoteException {
        super();
    }

    @Override
    public void handleMessage(String message) throws RemoteException { messages.add(message); }

    @Override
    public void ping() throws RemoteException { }

    /**
     * This method pops off the string, emptying this object of any information.
     * @return the contained string.
     * @throws NullPointerException if no string is contained.
     */
    public String getString() {
        if (messages.isEmpty()) {
            throw new NoSuchElementException();
        }
        // return the next message in queue
        return messages.poll();
    }

    /**
     * Use this to check if this object has a string, before getting it.
     * @return true if this object contains a string.
     */
    public boolean hasString() {
        return !(messages.isEmpty());
    }
}
