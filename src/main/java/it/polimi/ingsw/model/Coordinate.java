package it.polimi.ingsw.model;

public class Coordinate {
    private final int x;
    private final int y;

    public Coordinate(int y, int x) {
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
        if (obj instanceof Coordinate o) {
            return this.x == o.x && this.y == o.y;
        }
        return false;
    }
}
