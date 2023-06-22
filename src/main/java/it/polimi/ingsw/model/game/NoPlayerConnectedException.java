package it.polimi.ingsw.model.game;

/**
 * This exception is thrown when there are no connected users
 * @author Giacomo Groppi
 */
public class NoPlayerConnectedException extends Exception {
    /**
     * Constructs a new instance of {@link NoPlayerConnectedException} with a default empty message.
     */
    public NoPlayerConnectedException () {
        super();
    }
}
