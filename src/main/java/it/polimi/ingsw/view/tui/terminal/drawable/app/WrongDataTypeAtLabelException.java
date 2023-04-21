package it.polimi.ingsw.view.tui.terminal.drawable.app;

public class WrongDataTypeAtLabelException extends RuntimeException {
    public WrongDataTypeAtLabelException(String label, String expectedTypeName) {
        super("Data at " + label + " is not a " + expectedTypeName);
    }
}
