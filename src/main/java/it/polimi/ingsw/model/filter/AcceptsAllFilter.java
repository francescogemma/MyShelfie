package it.polimi.ingsw.model.filter;

import it.polimi.ingsw.model.tile.TileColor;

/**
 * Filter which accepts every finite sequence of tile colors.
 */
public class AcceptsAllFilter implements Filter {
    @Override
    public boolean add(TileColor tileColor) {
        return false;
    }

    @Override
    public void forgetLastTile() { }

    @Override
    public boolean isSatisfied() {
        return true;
    }

    @Override
    public void clear() {

    }
}
