package it.polimi.ingsw.model;

public interface Filter {
    boolean add(Tile tile);
    void forgetLastTile();
    boolean isSatisfied();
    void clear();
}
