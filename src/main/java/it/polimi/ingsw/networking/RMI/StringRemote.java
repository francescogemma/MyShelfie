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
     * This should be the only remote method we need: a simple "acceptor" method to receive a string.
     *
     * @param message is the string that will be received and stored.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     */
    void handleMessage(String message) throws RemoteException;

    /**
     * This should be used for some sort of RMI keep-alive implementation. The method itself should do nothing.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     */
    boolean ping() throws RemoteException;
}
