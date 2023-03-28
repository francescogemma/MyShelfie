package it.polimi.ingsw.model.bag;
import it.polimi.ingsw.model.Tile;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

/**
 * This represents the bag for a game, which contains all 6 types of {@link Tile tile} (22 tiles per type).
 * It's not possible to refill the bag
 * @author Giacomo Groppi
*/
public class Bag {
    protected static final int typeTile = 6;
    protected static final int tilePerTyle = 22;
    private int remaining;
    private final int []remainingTile = new int[typeTile];
    private int lastExtraction;

    /**
     * Constructor of the class.
     * It initializes all 22*6 {@link Tile tile}
     * */
    public Bag() {
        Arrays.fill(remainingTile, tilePerTyle);
        lastExtraction = -1;
        this.remaining = tilePerTyle * typeTile;
    }

    public Bag (final Bag bag) {
        System.arraycopy(bag.remainingTile, 0, this.remainingTile, 0, remainingTile.length);
        this.remaining = bag.remaining;
        this.lastExtraction = bag.lastExtraction;
    }

    /**
     * The function selects a random {@link Tile tile} from the bag and removes it.
     * @return a random {@link Tile tile}
     * @see Tile
     * */
    public Tile getRandomTile() {
        if (this.isEmpty()) {
            throw new IllegalStateException("Bag is empty");
        }

        final int index = new Random().nextInt(typeTile);

        for (int i = 0; i < typeTile; i++) {
            final int realIndex = (index + i) % typeTile;
            if (getAvailable(realIndex) != 0) {
                return this.removeAndReturn(realIndex);
            }
        }

        throw new InternalError("Internal error: loop should never finish");
    }

    /**
     * The function removes the Tile at position index
     * @param index index of extraction
     * @throws IllegalArgumentException If there is no Tile left at index i
     */
    private Tile removeAndReturn(int index) {
        if (this.remainingTile[index] < 1) {
            throw new IllegalArgumentException("Index not valid");
        }

        this.remaining --;
        this.remainingTile[index] --;
        lastExtraction = index;
        return getTileByIndex(index);
    }

    private static Tile getTileByIndex(int index) {
        if (index < 0 || index > Tile.values().length)
            throw new IllegalArgumentException("Index out of bounds [index: " + index + "]");

        return Tile.values()[index];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return Arrays.equals(this.remainingTile, ((Bag) o).remainingTile);
    }

    /**
     * This function reintroduces the last drawn Tile back into the bag.
     * @throws RuntimeException If there has been no draw or the last draw has already been restored.
     * */
    public void forgetLastExtraction () {
        if (lastExtraction == -1) {
            throw new RuntimeException("There has been no draw, or the last draw has already been restored.");
        }

        this.remaining ++;
        this.remainingTile[this.lastExtraction] ++;
        this.lastExtraction = -1;
    }

    /**
     * @param index index of interrogation
     * @return The number of remaining Tiles at index i.
     * */
    protected int getAvailable(int index) {
        return this.remainingTile[index];
    }

    /**
     * @return return the number of tiles left in the class.
     * */
    protected int getRemaining() {
        return this.remaining;
    }

    public boolean isEmpty() {
        return this.getRemaining() == 0;
    }
}
