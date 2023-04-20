package it.polimi.ingsw.networking.RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface is needed to declare remote methods. {@link RMIConnection RMIConnection} should implement this.
 * Classes that implement this interface can receive strings and be pinged.
 *
 * @author Michele Miotti
 */
public interface StringRemote extends Remote {
    /**
     * A simple "acceptor" method to receive a string.
     * @param message is the string that will be received and stored.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     */
    void handleMessage(String message) throws RemoteException;

    /**
     * This should be used for an RMI keep-alive implementation.
     * @return true if the pinged object is connected.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     */
    boolean ping() throws RemoteException;
}
