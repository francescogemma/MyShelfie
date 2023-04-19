package it.polimi.ingsw.networking.TCP;

import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.ConnectionException;
import it.polimi.ingsw.networking.DisconnectedException;

import java.io.*;
import java.net.Socket;
import java.util.Stack;
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
    private final Socket socket;
    private final DataOutputStream  out;
    private final DataInputStream in;
    private boolean disconnected;
    private final Object lock = new Object();
    private final Stack<String> receivedMessages;

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
     */
    public TCPConnection(String address, int port) throws SocketCreationException {
        disconnected = false;
        receivedMessages = new Stack<>();

        try {
            socket = new Socket(address, port);

            socket.setTcpNoDelay(true);
            socket.setSoTimeout(5000);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
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
        receivedMessages = new Stack<>();

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
                if(disconnected) {
                    throw new DisconnectedException("disconnected while waiting for a message");
                }
            }

            // return the first message in the receivedMessages stack
            return receivedMessages.pop();
        }
    }

    /**
     * Sends a heartbeat message every 2.5 seconds to the other side of the connection
     * to check if the connection is still alive.
     */
    private void heartbeat() {
        // create a new Timer and schedule a new TimerTask that sends a heartbeat message every 2.5 seconds
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
                String read;
                try {
                    socket.setSoTimeout(5000);
                    read = in.readUTF();
                    if(!read.equals("heartbeat")) {
                        synchronized(lock) {
                            receivedMessages.push(read);
                            lock.notifyAll();
                        }
                    }
                } catch(Exception e) {
                    synchronized(lock) {
                        disconnected = true;
                        lock.notifyAll();
                    }
                    break;
                }
            }
        });
        reader.start();
    }
}
