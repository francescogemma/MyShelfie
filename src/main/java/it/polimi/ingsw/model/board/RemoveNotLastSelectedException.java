package it.polimi.ingsw.model.board;

/**
 * Exception thrown when we try to deselect a tile which is not the last one that has been selected.
 *
 * @author Giacomo Groppi
 */
public class RemoveNotLastSelectedException extends RuntimeException{
    /**
     * Constructor of the class.
     */
    public RemoveNotLastSelectedException() {
        super();
    }
}
