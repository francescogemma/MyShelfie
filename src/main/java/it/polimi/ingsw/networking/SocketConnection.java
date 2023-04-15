package it.polimi.ingsw.networking;

/**
 * {@link Connection Connection} class that handles Socket communication.
 *
 * @author Francesco Gemma
 */
public class SocketConnection implements Connection {
    @Override
    public void send(String string) throws DisconnectedException {

    }

    @Override
    public String receive() throws DisconnectedException {
        return null;
    }
}
