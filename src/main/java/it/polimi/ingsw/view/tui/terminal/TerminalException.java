package it.polimi.ingsw.view.tui.terminal;

/**
 * Exception thrown when something unexpected happens during updates to terminal configuration (like setting raw mode)
 * or while retrieving the current {@link TerminalSize}.
 */
public class TerminalException extends RuntimeException {
    /**
     * Constructor of the class.
     * Initializes the exception message.
     *
     * @param message is the exception message.
     */
    public TerminalException(String message) {
        super(message);
    }
}
