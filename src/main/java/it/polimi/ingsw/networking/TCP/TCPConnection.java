package it.polimi.ingsw.networking.TCP;

import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.DisconnectedException;

import java.io.*;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * {@link Connection Connection} class that handles TCP communication via {@link Socket Socket}.
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
     */
    public TCPConnection(String address, int port) {
        disconnected = false;

        try {
            socket = new Socket(address, port);

            socket.setTcpNoDelay(true);

            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch(IOException e) {
            throw new SocketCreationExeption("error while creating socket", e);
        }

        heartbeat();
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
     */
    public TCPConnection(Socket socket) {
        disconnected = false;
        this.socket = socket;

        try {
            out = new DataOutputStream(this.socket.getOutputStream());
            in = new DataInputStream(this.socket.getInputStream());
        } catch(IOException e) {
            throw new SocketCreationExeption("error while creating socket", e);
        }

        heartbeat();
    }

    @Override
    public void send(String string) throws DisconnectedException {
        // if the connection was already broken, throw a DisconnectedException
        if(disconnected) {
            throw new DisconnectedException("already disconnected");
        }

        // send the string, if an IOException is thrown, throw a DisconnectedException
        try {
            synchronized(out) {
                out.writeUTF(string);
                out.flush();
            }
        } catch(IOException e) {
            throw new DisconnectedException("sending while disconnected", e);
        }
    }

    @Override
    public String receive() throws DisconnectedException {
        String read;

        // read a string from the socket, if an IOException is thrown, throw a DisconnectedException
        try {
            synchronized(in) {
                while((read = in.readUTF()).equals("heartbeat")) {}
            }
            return read;
        } catch(IOException e) {
            throw new DisconnectedException("receiving while disconnected", e);
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
                    // if we are disconnected, cancel the timer and set the disconnected field to true
                    disconnected = true;
                    timer.cancel();
                }
            }
        }, 0, 2500);
    }
}
