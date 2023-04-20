package it.polimi.ingsw.networking;

/**
 * Type that can send a string to a specific target, and receive a string from the same target.
 * On construction, an {@link ConnectionAcceptor acceptor} is called, and a Connection pair is created and linked.
 *
 * @author Francesco Gemma
 * @author Michele Miotti
 */
public interface Connection {
    /**
     * Send the provided string to the target assigned by the acceptor.
     * @param string will be sent to the target provided by the acceptor.
     */
    void send(String string) throws DisconnectedException;

    /**
     * This method will return the string that has been received from another connection.
     * @return a string that has been received from another connection.
     */
    String receive() throws DisconnectedException;

    /**
     * This method stops all threads relating to the pair this connection is assigned to,
     * and shuts the connection down.
     */
    void disconnect();
}
