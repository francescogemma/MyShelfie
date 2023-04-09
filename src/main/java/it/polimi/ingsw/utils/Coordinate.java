package it.polimi.ingsw.utils;

import java.util.List;
import java.util.Collection;

public class Coordinate {
    private final int col;
    private final int row;

    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public static List<Coordinate> toList(Collection<int []> data) {
        return data
                .stream()
                .map(p -> new Coordinate(p[0], p[1]))
                .toList();
    }

    public int getCol() {
        return this.col;
    }

    public int getRow() {
        return this.row;
    }

    public Coordinate down () {
        return new Coordinate(this.row + 1, col);
    }

    public Coordinate right () {
        return new Coordinate(this.row, col + 1);
    }

    public Coordinate top () {
        return new Coordinate(this.row - 1, this.col);
    }

    public Coordinate left () {
        return new Coordinate(this.row, this.col - 1);
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

    @Override
    public String toString() {
        return "[" + this.row + " " + col + "]";
    }
}
