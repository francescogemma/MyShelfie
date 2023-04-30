package it.polimi.ingsw.networking.RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
public interface RemoteQueue extends Remote {
    void add(String string) throws RemoteException;
    String poll() throws RemoteException;
}
