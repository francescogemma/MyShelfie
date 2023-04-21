package it.polimi.ingsw.view.tui.terminal.drawable.app;

public class NoDataAtLabelException extends RuntimeException {
    public NoDataAtLabelException(String label) {
        super("There is no data corresponding to " + label);
    }
}
