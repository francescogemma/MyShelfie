package it.polimi.ingsw.networking;

/**
 * {@link Connection Connection} class that handles RMI communication.
 *
 * @author Michele Miotti
 */
public class RMIConnection implements Connection {

    @Override
    public void send(String string) throws DisconnectedException {

    }

    @Override
    public String receive() throws DisconnectedException {
        return null;
    }
}
