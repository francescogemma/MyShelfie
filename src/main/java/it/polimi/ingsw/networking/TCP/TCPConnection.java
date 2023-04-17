package it.polimi.ingsw.networking.TCP;

import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.DisconnectedException;

import java.io.*;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * {@link Connection Connection} class that handles Socket communication.
 *
 * @author Francesco Gemma
 */
public class TCPConnection implements Connection {
    Socket socket;
    final DataOutputStream  out;
    final DataInputStream in;
    boolean disconnected;

    public TCPConnection(String address, int port) {
        disconnected = false;

        try {
            socket = new Socket(address, port);

            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            socket.setTcpNoDelay(true);
        } catch(IOException e) {
            throw new SocketCreationExeption("error while creating socket", e);
        }

        heartbeat();
    }

    public TCPConnection(Socket socket) {
        disconnected = false;
        this.socket = socket;

        try {
            out = new DataOutputStream(this.socket.getOutputStream());
            in = new DataInputStream(this.socket.getInputStream());

            this.socket.setTcpNoDelay(true);
        } catch(IOException e) {
            throw new SocketCreationExeption("error while creating socket", e);
        }

        heartbeat();
    }

    @Override
    public void send(String string) throws DisconnectedException {
        if(disconnected) {
            throw new DisconnectedException("already disconnected");
        }

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
        try {
            synchronized(in) {
                while((read = in.readUTF()).equals("heartbeat")) {}
            }
            return read;
        } catch(IOException e) {
            throw new DisconnectedException("receiving while disconnected", e);
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
                    disconnected = true;
                    timer.cancel();
                }
            }
        }, 0, 2500);
    }
}
