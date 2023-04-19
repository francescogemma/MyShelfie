package it.polimi.ingsw.networking.RMI;

import it.polimi.ingsw.networking.ConnectionException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Objects implementing this interface will act as "connection creators".
 * They will receive a pairing request from a connection, give them back a name,
 * and wait for a request to generate a new connection "sever-side".
 *
 * @author Michele Miotti
 */
public interface NameProvidingRemote extends Remote {
    /**
     * This method will be called remotely by the connection that wants to be paired.
     * It will return the name of the pair's connection (without any suffix).
     * For example, it might return "CONN2000", to which someone else will append "CLIENT".
     *
     * @return a string which represents the name of the newly created connection pair.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     */
    String getNewCoupleName() throws RemoteException;

    /**
     * Method used to tell the server to create a new connection remotely, that will be paired with
     * the client-side connection
     *
     * @param coupleName is the name of the connection couple, without any suffix.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     * @throws ConnectionException will be thrown if a failure occurs in the process of creating a new Connection.
     */
    void createRemoteConnection(String coupleName) throws RemoteException, ConnectionException;
}
