package it.polimi.ingsw.networking;

/**
 * Low level object that can send a string to a specific target, and receive a string.
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
}
