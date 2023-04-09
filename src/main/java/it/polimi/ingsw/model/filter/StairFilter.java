package it.polimi.ingsw.model.filter;

import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.utils.Coordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Giacomo Groppi
* */
public class StairFilter implements Filter{
    private final List<Boolean[]> position;
    private final List<Coordinate> coordinates = new ArrayList<>();
    private int index;
    private boolean forNowOk;

    public StairFilter(List<Boolean[]> position) {
        this.position = new ArrayList<>();

        for (int i = 0; i < position.size(); i++) {
            this.position.add(new Boolean[position.get(i).length]);

            for (int j = 0; j < position.get(i).length; j++) {
                final Boolean value = position.get(i)[j];
                if (value != null) {
                    this.position.get(i)[j] = value;
                    this.coordinates.add(new Coordinate(i, j));
                }
            }
        }

        clear();
    }

    @Override
    public boolean add(TileColor tileColor) {
        if (index >= coordinates.size()) {
            forNowOk = false;
        }

        if (!this.forNowOk) {
            return false;
        }

        final Coordinate coordinate = coordinates.get(this.index);
        final boolean value = position.get(coordinate.getRow())[coordinate.getCol()];

        if (tileColor == TileColor.EMPTY) {
            if (value) {
                forNowOk = false;
            }
        } else {
            if (!value) {
                forNowOk = false;
            }
        }

        this.index ++;

        return false;
    }

    @Override
    public void forgetLastTile() {
        assert false;
    }

    @Override
    public boolean isSatisfied() {
        final int size = coordinates.size();
        return forNowOk && index == size;
    }

    @Override
    public void clear() {
        this.index = 0;
        forNowOk = true;
    }
}
