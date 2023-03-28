package it.polimi.ingsw.model.filter;

import it.polimi.ingsw.model.Tile;

public class AcceptsAllFilter implements Filter {
    @Override
    public boolean add(Tile tile) {
        return false;
    }

    @Override
    public void forgetLastTile() {

    }

    @Override
    public boolean isSatisfied() {
        return true;
    }

    @Override
    public void clear() {

    }
}
