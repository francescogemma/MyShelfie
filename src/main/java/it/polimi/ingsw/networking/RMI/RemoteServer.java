package it.polimi.ingsw.networking.RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Objects implementing this interface will act as "connection creators".
 * They will receive a pairing request from a connection, and give them back a name
 * after binding two queues to the registry.
 *
 * @author Michele Miotti
 */
public interface RemoteServer extends Remote {
    /**
     * This method will be called remotely by the connection that wants to be paired.
     * It will return the name of the pair's connection (undecorated).
     * For example, it might return "QUEUE27", to which someone else might add a prefix.
     *
     * @return a string which represents the name of the newly created connection pair.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     */
    String getBoundName() throws RemoteException;
}
