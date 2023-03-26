package it.polimi.ingsw.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

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
                .map((p) -> new Coordinate(p[0], p[1]))
                .toList();
    }

    public int getCol() {
        return this.col;
    }

    public int getRow() {
        return this.row;
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
