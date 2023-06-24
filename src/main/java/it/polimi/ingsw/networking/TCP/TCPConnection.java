package it.polimi.ingsw.networking.TCP;

import it.polimi.ingsw.networking.*;

import java.io.*;
import java.net.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Timer;
import java.util.TimerTask;

/**
 * {@link Connection Connection} class that handles TCP communication via {@link Socket Socket}.
 *
 * @see Connection
 * @see Socket
 *
 * @author Francesco Gemma
 */
public class TCPConnection implements Connection {
    /**
     * {@link Socket Socket} used for communication.
     */
    private final Socket socket;

    /**
     * {@link DataOutputStream DataOutputStream} used to write strings to the socket.
     */
    private final DataOutputStream  out;

    /**
     * {@link DataInputStream DataInputStream} used to read strings from the socket.
     */
    private final DataInputStream in;

    /**
     * Boolean value that indicates if the connection is broken.
     */
    private boolean disconnected;

    /**
     * {@link Object Object} used as a lock.
     */
    private final Object lock = new Object();

    /**
     * {@link Deque Deque} of {@link String Strings} that contains all the messages
     * received from the other side of the connection.
     */
    private final Deque<String> receivedMessages;

    /**
     * Creates a new {@link TCPConnection TCPConnection} object intended to be used as a client.
     * It creates a new {@link Socket}, and it sets it to NoDelay mode for better performance.
     * It sets also the {@link TCPConnection#out} and {@link TCPConnection#in} fields respectively
     * to {@link Socket#getOutputStream()} and {@link Socket#getInputStream()}
     * wrapped in a {@link DataOutputStream DataOutputStream} for easier string writing and reading.
     * <p>
     * It also starts a {@link Timer Timer} that sends a heartbeat message every 2.5 seconds to the other side of the connection
     * to check if the connection is still alive.
     *
     * @param address address of the machine hosting the server socket
     * @param port port of the server socket
     * @throws SocketCreationException if an error occurs while creating the socket
     * @throws ServerNotFoundException if the server ip is not found
     */
    public TCPConnection(String address, int port) throws SocketCreationException, ServerNotFoundException {
        if(port < 1024 || port > 49151) {
            throw new BadPortException("port " + port + " out of range [1024, 49151]");
        }

        disconnected = false;
        receivedMessages = new ArrayDeque<>();

        try {
            socket = new Socket();
            socket.connect(new java.net.InetSocketAddress(address, port), 2000);

            socket.setTcpNoDelay(true);
            socket.setSoTimeout(5000);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch(IllegalArgumentException e) {
            throw new BadHostException("address is null", e);
        } catch(UnknownHostException e) {
            throw new BadHostException("unknown host: " + address, e);
        } catch(SocketTimeoutException e) {
            throw new BadHostException("server: " + address + " not found listening on port: " + port + " (timeout expired)", e);
        } catch(ConnectException e) {
            throw new BadPortException("server: " + address + " not found listening on port: " + port, e);
        } catch(IOException e) {
            throw new SocketCreationException("error while creating socket", e);
        }

        heartbeat();
        reader();
    }

    /**
     * Creates a new {@link TCPConnection TCPConnection} object intended to be used server-side.
     * It sets the {@link TCPConnection#socket} field to the given {@link Socket Socket}.
     * It sets also the {@link TCPConnection#out} and {@link TCPConnection#in} fields respectively
     * to {@link Socket#getOutputStream()} and {@link Socket#getInputStream()}
     * wrapped in a {@link DataOutputStream DataOutputStream} for easier string writing and reading.
     * <p>
     * It also starts a {@link Timer Timer} that sends a heartbeat message every 2.5 seconds to the other side of the connection
     * to check if the connection is still alive.
     *
     * @param socket socket to be used for communication
     * @throws SocketCreationException if an error occurs while setting the input/output streams of the socket
     */
    public TCPConnection(Socket socket) throws SocketCreationException {
        disconnected = false;
        receivedMessages = new ArrayDeque<>();

        this.socket = socket;

        try {
            in = new DataInputStream(this.socket.getInputStream());
            out = new DataOutputStream(this.socket.getOutputStream());
            socket.setSoTimeout(5000);
        } catch(IOException e) {
            throw new SocketCreationException("error while setting input/output streams", e);
        }

        heartbeat();
        reader();
    }

    @Override
    public void send(String string) throws DisconnectedException {
        synchronized(lock) {
            // if the connection was already broken, throw a DisconnectedException
            if(disconnected) {
                throw new DisconnectedException("already disconnected");
            }

            // send the string, if an IOException is thrown, throw a DisconnectedException
            try {
                out.writeUTF(string);
                out.flush();
            } catch(IOException e) {
                throw new DisconnectedException("sending while disconnected", e);
            }
        }
    }

    @Override
    public String receive() throws DisconnectedException {
        synchronized(lock) {
            // if the connection was already broken, throw a DisconnectedException
            if(disconnected) {
                throw new DisconnectedException("already disconnected");
            }

            // if there are no messages in the receivedMessages stack, wait for a message
            while(receivedMessages.isEmpty()) {
                try {
                    lock.wait();
                } catch(InterruptedException e) {
                    throw new DisconnectedException("interrupted while waiting for a message", e);
                }
                // if the connection was broken while waiting, we are notified, throw a DisconnectedException
                if(disconnected) {
                    throw new DisconnectedException("disconnected while waiting for a message");
                }
            }

            // return the first message in the receivedMessages stack
            return receivedMessages.poll();
        }
    }

    /**
     * Crate a timer that sends a heartbeat message every 2.5 seconds to the other side of the connection
     * to check if the connection is still alive.
     */
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

    /**
     * Creates a new thread that continuously reads messages from the socket.
     * If the message received is not a heartbeat, it is added to the {@link TCPConnection#receivedMessages receivedMessages} stack
     * and the {@link TCPConnection#receive()} method will be able to read it.
     * <p>
     * If the message received is a heartbeat, it is simply discarded,
     * but if we don't receive any message for 5 seconds, the connection is considered broken.
     * <p>
     * This method will also close the socket if a disconnection is detected.
     */
    private void reader() {
        Thread reader = new Thread(() -> {
            while(true) {
                synchronized(lock) {
                    if(disconnected) {
                        try {
                            socket.close();
                        } catch(IOException ignored) {}
                        return;
                    }
                }

                String read;
                try {
                    socket.setSoTimeout(5000);
                    read = in.readUTF();
                    if(!read.equals("heartbeat")) {
                        /*
                         * the received message is not a heartbeat, add it to the receivedMessages stack to be able to read it.
                         * we notify the receive() method that it the stack is not empty anymore, and it can read the message
                         */
                        synchronized(lock) {
                            receivedMessages.add(read);
                            lock.notifyAll();
                        }
                    }
                } catch(Exception e) {
                    /*
                     * if an exception is thrown, the connection is considered broken, we set the disconnected field to true,
                     * and we notify the receive() method that it can throw a DisconnectedException
                     */
                    synchronized(lock) {
                        disconnected = true;
                        lock.notifyAll();
                    }
                }
            }
        });
        reader.start();
    }

    /*
     * Sets the disconnected field to true and notifies an eventual
     * receive() method waiting for a message, that the connection will be closed, so it can throw a DisconnectedException.
     * The reader() method will then close the socket.
     */
    @Override
    public void disconnect() {
        synchronized(lock) {
            disconnected = true;
            lock.notifyAll();
        }
    }
}
