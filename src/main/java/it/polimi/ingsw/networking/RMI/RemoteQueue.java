package it.polimi.ingsw.networking.RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface that supports queue addition and queue polling.
 *
 * @author Michele Miotti
 */
public interface RemoteQueue extends Remote {
    /**
     * Adds a string to the queue.
     * @param string will be added to the queue.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     */
    void add(String string) throws RemoteException;

    /**
     * Polls a string to the queue (the oldest added string), removing it.
     * @return the most old element in the queue will be removed and returned
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     */
    String poll() throws RemoteException;
}