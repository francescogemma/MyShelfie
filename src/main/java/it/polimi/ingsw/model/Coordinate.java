package it.polimi.ingsw.model;

public class Coordinate {
    private final int col;
    private final int row;

    public Coordinate(int row, int col) {
        this.col = col;
        this.row = row;
    }

    public int getCol() {
        return this.col;
    }

    public int getRow() {
        return this.row;
    }

    public int getX () {
        return getCol();
    }

    public int getY () {
        return this.getRow();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj instanceof Coordinate o) {
            return this.col == o.col && this.row == o.row;
        }
        return false;
    }
}
