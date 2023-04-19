package it.polimi.ingsw.networking;

/**
 * Low level object that can send a string to a specific target, and receive a string from the same target.
 * On construction, an {@link ConnectionAcceptor acceptor} is called, and a Connection pair is created and linked.
 *
 * @author Francesco Gemma
 * @author Michele Miotti
 */
public interface Connection {
    /**
     * Send the provided string to the target given to the constructor.
     * @param string will be sent to the target provided to the constructor.
     */
    void send(String string) throws DisconnectedException;

    /**
     * This method will return the string that has been received over the network.
     * @return a string that has been received over the network.
     */
    String receive() throws DisconnectedException;

    /**
     * This method stops all threads relating to the pair this connection is assigned to,
     * and shuts the connection down.
     */
    void disconnect();
}
