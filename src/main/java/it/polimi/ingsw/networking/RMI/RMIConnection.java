package it.polimi.ingsw.networking.RMI;

import it.polimi.ingsw.networking.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * {@link Connection Connection} class that represents one side of an RMI pair of connections.
 *
 * @author Michele Miotti
 */
public class RMIConnection implements Connection {
    /**
     * This queue is a remote object, bound to the server's registry.
     * A client will add strings to this queue, the server will poll them.
     */
    private final RemoteQueue addQueue;

    /**
     * This queue is a remote object, bound to the server's registry.
     * A client will poll string from this queue, the server will add them.
     */
    private final RemoteQueue pollQueue;
    private final Queue<String> pendingMessages;

    /**
     * Used to keep track of connection state. Methods "send" and "receive" can only work if this
     * boolean is false.
     */
    private boolean disconnected;

    /**
     * Lock needed to protect portions of object state that need to be modified by threads, such as
     * the "disconnected" boolean, and both remote queues.
     */
    private final Object lock = new Object();

    /**
     * This constructor creates a connection with some {@link it.polimi.ingsw.networking.ConnectionAcceptor acceptor}.
     * This acceptor will then return the name of a newly created remote queue pair, through a remote method.
     * This constructor should be used client-side.
     *
     * @param address is the address of the server's host.
     * @param port is the port used by {@link it.polimi.ingsw.networking.ConnectionAcceptor the server} for RMI communication.
     * @throws ServerNotFoundException will be thrown if a failure occurs in the process of connecting to the server.
     */
    public RMIConnection(String address, int port) throws ServerNotFoundException {
        if (port < 1024 || port > 49151) {
            throw new BadPortException("port " + port + " out of range");
        }

        if (address == null) {
            throw new BadHostException("host address is null");
        }

        pendingMessages = new LinkedList<>();
        disconnected = false;

        try {
            // get the server object, and ask to reserve a new name for the couple.
            Registry registry = LocateRegistry.getRegistry(address, port);
            RemoteServer remoteServer = (RemoteServer) registry.lookup("SERVER");

            String boundName = remoteServer.getBoundName();
            addQueue = (RemoteQueue) registry.lookup("ADD_" + boundName);
            pollQueue = (RemoteQueue) registry.lookup("POLL_" + boundName);
        } catch (Exception exception) {
            throw new ServerNotFoundException(exception.getMessage());
        }

        heartbeat();
        reader();
    }

    /**
     * This constructor does NOT request names to an {@link it.polimi.ingsw.networking.ConnectionAcceptor acceptor},
     * and should only be constructed BY an acceptor to create a connection that is already looking for the right queue
     * on the registry. Of course, this constructor needs to know the undecorated queue name in order to work.
     * This method should be called server-side.
     *
     * @param address is the address of the server's host.
     * @param port is the port used by {@link it.polimi.ingsw.networking.ConnectionAcceptor the server} for RMI communication.
     * @param boundName is the name of the undecorated queue that should already be bound to the registry.
     * @throws ServerNotFoundException will be thrown if a failure occurs in the process of connecting to the server.
     */
    public RMIConnection(String address, int port, String boundName) throws ServerNotFoundException {
        pendingMessages = new LinkedList<>();
        disconnected = false;

        try {
            Registry registry = LocateRegistry.getRegistry(address, port);
            addQueue = (RemoteQueue) registry.lookup("POLL_" + boundName);
            pollQueue = (RemoteQueue) registry.lookup("ADD_" + boundName);
        } catch (Exception exception) {
            throw new ServerNotFoundException(exception.getMessage());
        }

        heartbeat();
        reader();
    }

    @Override
    public void send(String string) throws DisconnectedException {
        synchronized (lock) {
            if (disconnected) {
                throw new DisconnectedException();
            }

            try {
                addQueue.add(string);
            } catch (RemoteException remoteException) {
                disconnect();
                throw new DisconnectedException(remoteException.getMessage());
            }
        }
    }

    @Override
    public String receive() throws DisconnectedException {
        synchronized(lock) {
            if(disconnected) {
                throw new DisconnectedException();
            }

            while(pendingMessages.isEmpty()) {
                try {
                    lock.wait();
                } catch(InterruptedException e) {
                    throw new DisconnectedException();
                }
                if(disconnected) {
                    throw new DisconnectedException();
                }
            }

            return pendingMessages.poll();
        }
    }

    @Override
    public void disconnect() {
        synchronized (lock) {
            disconnected = true;
            lock.notifyAll();
        }
    }

    private void heartbeat() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    send("heartbeat");
                } catch (DisconnectedException e) {
                    timer.cancel();
                }
            }
        }, 0, 2500);
    }

    private void reader() {
        Thread reader = new Thread(() -> {
            while(true) {
                Callable<String> poller = new Callable<String>() {
                    @Override
                    public String call() {
                        String read = null;
                        try {
                            read = pollQueue.poll();
                        } catch (RemoteException remoteException) {
                            disconnect();
                        }

                        return read;
                    }
                };

                final ExecutorService executor = Executors.newSingleThreadExecutor();
                final Future future = executor.submit(poller);
                executor.shutdown();

                String read;
                try {
                    read = (String) future.get(5000, TimeUnit.MILLISECONDS);
                    if (read == null) {
                        disconnect();
                        return;
                    }
                }
                catch (Exception e) {
                    disconnect();
                    return;
                }
                if (!executor.isTerminated()) {
                    executor.shutdownNow();
                }

                if(!read.equals("heartbeat")) {
                    synchronized(lock) {
                        pendingMessages.add(read);
                        lock.notifyAll();
                    }
                }
            }
        });
        reader.start();
    }
}