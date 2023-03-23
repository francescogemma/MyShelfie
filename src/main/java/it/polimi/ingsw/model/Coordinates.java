package it.polimi.ingsw.model;

import java.io.StreamCorruptedException;

public class Coordinates {
    private final int x;
    private final int y;

    public Coordinates(int y, int x) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj instanceof Coordinates o) {
            return this.x == o.x && this.y == o.y;
        }
        return false;
    }
}
