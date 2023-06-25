package it.polimi.ingsw.view.gui;

/**
 * Exception thrown when a fxml loader fails to load a resource.
 */
public class LoaderException extends RuntimeException {
    /**
     * Creates a new exception.
     */
    public LoaderException() { super(); }

    /**
     * Creates a new exception with the specified detail message.
     *
     * @param s the detail message
     */
    public LoaderException(String s) { super(s); }

    /**
     * Creates a new exception with the specified detail message and cause.
     *
     * @param s the detail message
     * @param cause the cause
     */
    public LoaderException(String s, Throwable cause) { super(s, cause); }
}
