package it.polimi.ingsw.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;

/**
 * The class represents a pair of row and column integers.
 * @author Giacomo Groppi
 * */
public class Coordinate {
    /**
     * The column value of the coordinate.
     */
    private final int col;

    /**
     * The row value of the coordinate.
     */
    private final int row;

    /**
     * Constructs a new Coordinate with the given row and column values.
     * @param row the row value of the coordinate
     * @param col the col value of the coordinate
     * */
    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Converts a collection of integer arrays into a list of Coordinates
     * Each integer array should have two elements, representing the row and column values.
     *
     * @param data the collection of integer arrays to convert
     * @throws IllegalArgumentException iff exists an element with lenght other than 2
     * @return a list of Coordinates
     */
    public static List<Coordinate> toList(Collection<int []> data) {
        List<Coordinate> c = new ArrayList<>();
        for (int[] d : data) {
            if (d.length != 2)
                throw new IllegalArgumentException("Illegal argument number for " + Arrays.toString(d));
            c.add(new Coordinate(d[0], d[1]));
        }

        return c;
    }

    /**
     * Gets the column value of the coordinate.
     *
     * @return the column value
     * */
    public int getCol() {
        return this.col;
    }

    /**
     * Gets the row value of the coordinate.
     *
     * @return the row value
     * */
    public int getRow() {
        return this.row;
    }

    /**
     * Returns a new Coordinate that represents the position below this one.
     *
     * @return a new Coordinate representing the position below this one
     * */
    public Coordinate down () {
        return new Coordinate(this.row + 1, col);
    }

    /**
     * Returns a new Coordinate that represents the position to the right of this one.
     *
     * @return a new Coordinate representing the position to the right of this one
     * */
    public Coordinate right () {
        return new Coordinate(this.row, col + 1);
    }

    /**
     * Returns a new Coordinate that represents the position above this one.
     *
     * @return a new Coordinate representing the position above this one
     * */
    public Coordinate top () {
        return new Coordinate(this.row - 1, this.col);
    }

    /**
     * Returns a new Coordinate that represents the position to the left of this one.
     *
     * @return a new Coordinate representing the position to the left of this one
     * */
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
