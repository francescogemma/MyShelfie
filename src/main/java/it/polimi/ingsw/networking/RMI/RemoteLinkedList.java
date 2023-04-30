package it.polimi.ingsw.networking.RMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Simple thread-safe LinkedList that can be accessed through remote method invocation.
 *
 * @author Michele Miotti
 */
public class RemoteLinkedList extends UnicastRemoteObject implements RemoteQueue {
    /**
     * (Rep) This queue will be encapsulated by the class itself.
     */
    private final Queue<String> stringQueue;

    /**
     * Lock used to handle multiple threads adding and polling concurrently.
     */
    private final Object lock = new Object();

    /**
     * Class constructor.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     */
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