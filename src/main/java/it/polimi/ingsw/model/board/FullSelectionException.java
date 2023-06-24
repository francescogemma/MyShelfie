package it.polimi.ingsw.model.board;

/**
 * Exception thrown when we have already selected three tiles from the board and try to select one more.
 */
public class FullSelectionException extends Exception {
    /**
     * Constructor of the class.
     *
     * @param s is the message of the exception.
     */
    public FullSelectionException(String s) {
        super(s);
    }

    /**
     * Constructor of the class.
     */
    public FullSelectionException(){}
}
