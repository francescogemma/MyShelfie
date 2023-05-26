package it.polimi.ingsw.view.tui.terminal.drawable.app;

/**
 * Exception thrown when there is no data with the specified label inside an {@link AppLayoutData}.
 */
public class NoDataAtLabelException extends RuntimeException {
    /**
     * @param label is the label which doesn't correspond to any data inside the {@link AppLayoutData}.
     */
    public NoDataAtLabelException(String label) {
        super("There is no data corresponding to " + label);
    }
}
