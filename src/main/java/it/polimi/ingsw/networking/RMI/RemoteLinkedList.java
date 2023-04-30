package it.polimi.ingsw.networking.RMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;

public class RemoteLinkedList extends UnicastRemoteObject implements RemoteQueue {
    private final Queue<String> stringQueue;
    private final Object lock = new Object();
    public RemoteLinkedList() throws RemoteException {
        stringQueue = new LinkedList<>();
    }

    @Override
    public void add(String string) throws RemoteException {
        synchronized (lock) {
            stringQueue.add(string);
            lock.notifyAll();
        }
    }

    @Override
    public String poll() throws RemoteException {
        synchronized (lock) {
            while (stringQueue.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
            return stringQueue.poll();
        }
    }
}
