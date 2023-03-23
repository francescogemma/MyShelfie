package it.polimi.ingsw.model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

class BagData {
    protected static final int typeTile = 6;
    protected static final int tilePerTyle = 22;
    private int remaining;
    private final int []remainingTile;

    public BagData() {
        remainingTile = new int[typeTile];

        Arrays.fill(remainingTile, tilePerTyle);
        this.remaining = tilePerTyle * typeTile;
    }

    protected int getRemaining() {
        return this.remaining;
    }

    protected void pop(int index) {
        if (this.remainingTile[index] < 1) {
            throw new IllegalArgumentException("Index not valid");
        }

        this.remaining --;
        this.remainingTile[index] --;
    }

    protected int getAvailable(final int index) {
        return this.remainingTile[index];
    }

    public boolean isEmpty() {
        return this.getRemaining() == 0;
    }
}

public class Bag extends BagData {

    public Bag() {
        super();
    }

    public Tile getRandomTile() {
        int i;
        if (this.getRemaining() == 0) {
            throw new IllegalStateException("Bag is empty");
        }

        int index = new Random().nextInt(typeTile);

        for (i = 0; i < typeTile; i++) {
            final int realIndex = (index + i) % typeTile;
            if (getAvailable(realIndex) != 0) {
                return this.removeAndReturn(realIndex);
            }
        }

        throw new InternalError("Internal error: loop should never finish");
    }

    private Tile removeAndReturn(int index) {
        this.pop(index);
        return getTileByIndex(index);
    }
    static private Tile getTileByIndex(int index) {
        if (index < 0 || index > Tile.values().length)
            throw new IllegalArgumentException("Index out of bounds [index: " + index + "]");

        return Tile.values()[index];
    }
}
