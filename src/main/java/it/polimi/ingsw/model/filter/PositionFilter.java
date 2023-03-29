package it.polimi.ingsw.model.filter;

import it.polimi.ingsw.model.Tile;

import java.util.Collection;


public class PositionFilter implements Filter{
    

    public PositionFilter(Collection<Collection<Boolean>> position) {

    }

    @Override
    public boolean add(Tile tile) {
        return false;
    }

    @Override
    public void forgetLastTile() {

    }

    @Override
    public boolean isSatisfied() {
        return false;
    }

    @Override
    public void clear() {

    }
}
