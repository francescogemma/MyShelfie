package it.polimi.ingsw.model.filter;

import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.utils.Coordinate;

/**
 * @author Giacomo Groppi
* */
public class StairFilter implements Filter{
    private final boolean [][]position;
    private Coordinate index;
    private boolean forNowOk;

    public StairFilter(boolean[][] position) {
        this.position = new boolean[position.length][position[0].length];
        System.arraycopy(position, 0, this.position, 0, position.length);
        clear();
    }

    @Override
    public boolean add(Tile tile) {
        if (!this.forNowOk) {
            return false;
        }

        // we should not receive this position
        if (tile == Tile.EMPTY) {
            if (this.position[index.getRow()][index.getCol()]) {
                forNowOk = false;
            }
        } else {
            if (!this.position[index.getRow()][index.getCol()]){
                forNowOk = false;
            }
        }

        if (index.getCol() - 1 == index.getRow()) {
            index = new Coordinate(index.down().getRow(), 0);
        } else {
            index = index.right();
        }

        return false;
    }

    @Override
    public void forgetLastTile() {
        assert false;
    }

    @Override
    public boolean isSatisfied() {
        return this.index.getCol() == this.position[0].length &&
                this.index.getRow() + 1 == this.position.length &&
                forNowOk;
    }

    @Override
    public void clear() {
        this.index = new Coordinate(0, 0);
        forNowOk = true;
    }
}
