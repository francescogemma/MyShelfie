package it.polimi.ingsw.model.game;

/**
 * This exception is thrown when an illegal game flow occurs within a class.
 * @author Giacomo Groppi
 */
public class IllegalFlowException extends Exception {
    /**
     * This constructor creates a new instance of {@link IllegalFlowException} with an empty default message.
     */
    public IllegalFlowException() {
        super();
    }

   /**
    * Constructs a new instance of {@link IllegalFlowException} with the specified cause message.
    *
    * @param cause The message explaining the cause of the exception.
    */
    public IllegalFlowException(String cause) {
        super(cause);
    }
}
