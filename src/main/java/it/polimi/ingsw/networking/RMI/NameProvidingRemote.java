package it.polimi.ingsw.networking.RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Objects implementing this interface will act as "connection creators".
 * They will receive a pairing request from a connection by getting their name, they will
 * generate a new connection, and give back their name to the original one.
 *
 * @author Michele Miotti
 */
public interface NameProvidingRemote extends Remote {
    /**
     * This method will be called remotely by the connection that wants to be paired.
     * It will return the name of the other connection.
     *
     * @param name is the name of the connection that is trying to get paired.
     * @return a string which represents the name of the newly created connection, to be paired with the first one.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     */
    String getRemoteConnectionName(String name) throws RemoteException;
}
