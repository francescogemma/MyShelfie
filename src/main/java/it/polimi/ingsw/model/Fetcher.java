package it.polimi.ingsw.model;

public interface Fetcher {
    Shelf next();
    boolean lastShelf();
    boolean hasFinished();
    boolean canFix();
}
