package it.polimi.ingsw.model.board;

public class RemoveNotLastSelectedException extends RuntimeException{
    public RemoveNotLastSelectedException() {
        super();
    }

    public RemoveNotLastSelectedException(String s) {
        super(s);
    }
}