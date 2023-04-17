package it.polimi.ingsw.networking.RMI;

import it.polimi.ingsw.networking.ConnectionAcceptor;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface is needed to declare remote methods. Both the {@link ConnectionAcceptor ConnectionAcceptor} and
 * the {@link RMIConnection RMIConnection} objects will need to implement this interface in order to work correctly.
 *
 * @author Michele Miotti
 */
public interface StringRemote extends Remote {
    /**
     * This should be the only remote method we need: a simple "acceptor" method to receive a string.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     */
    void handleMessage(String message) throws RemoteException;
}
