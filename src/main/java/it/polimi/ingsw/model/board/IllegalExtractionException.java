package it.polimi.ingsw.model.board;

/**
 * Exception thrown when we perform an illegal extraction from the {@link Board} while adding the
 * tiles to a {@link it.polimi.ingsw.model.bookshelf.Bookshelf}.
 */
public class IllegalExtractionException extends Exception {
    /**
     * Constructor of the class.
     *
     * @param s is the message of the exception.
     */
    public IllegalExtractionException(String  s) {
        super(s);
    }

    /**
     * Constructor of the class.
     */
    public IllegalExtractionException(){}
}
