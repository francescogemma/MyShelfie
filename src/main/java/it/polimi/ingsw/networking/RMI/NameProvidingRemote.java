package it.polimi.ingsw.networking.RMI;

import it.polimi.ingsw.networking.ConnectionException;

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
     * It will return the name of the other connection, without any suffix.
     * For example, it might receive "CONN2000", to which someone else will append "CLIENT".
     *
     * @return a string which represents the name of the newly created connection, to be paired with the first one.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     */
    String getNewCoupleName() throws RemoteException;

    /**
     * Method used to tell the server to create a new connection remotely, that will be paired with
     * the client-side connection
     *
     * @param name is the name of the connection couple, without any suffix.
     */
    void createRemoteConnection(String name) throws RemoteException, ConnectionException;
}
