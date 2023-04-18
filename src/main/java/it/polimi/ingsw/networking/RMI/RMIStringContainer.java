package it.polimi.ingsw.networking.RMI;

import java.rmi.RemoteException;

/**
 * Simple object to encapsulate all RMI-related behaviour. The handleMessage method is invoked remotely, and
 * some other object needs to use the getString and hasString methods to recover the message.
 */
public class RMIStringContainer implements StringRemote {
    /**
     * Received message will be stored here.
     */
    String string;

    /**
     * Object constructor that simply sets the string to null.
     */
    public RMIStringContainer() { string = null; }

    @Override
    public void handleMessage(String message) throws RemoteException { string = message; }

    @Override
    public void ping() throws RemoteException { }

    /**
     * This method pops off the string, emptying this object of any information.
     * @return the contained string.
     * @throws NullPointerException if no string is contained.
     */
    public String getString() {
        if (string == null) {
            throw new NullPointerException();
        }
        // clear the string attribute before returning
        String returnedString = string;
        string = null;

        return returnedString;
    }

    /**
     * Use this to check if this object has a string, before getting it.
     * @return true if this object contains a string.
     */
    public boolean hasString() {
        return !(string == null);
    }
}
