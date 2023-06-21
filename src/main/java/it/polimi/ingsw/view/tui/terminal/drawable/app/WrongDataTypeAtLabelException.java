package it.polimi.ingsw.view.tui.terminal.drawable.app;

/**
 * Exception thrown when the data with the specified label has a different concrete type than expected.
 *
 * @author Cristiano Migali
 */
public class WrongDataTypeAtLabelException extends RuntimeException {
    /**
     * @param label is the label associated with the desired data.
     * @param expectedTypeName is the name of the expected concrete type of the desired data.
     */
    public WrongDataTypeAtLabelException(String label, String expectedTypeName) {
        super("Data at " + label + " is not a " + expectedTypeName);
    }
}
