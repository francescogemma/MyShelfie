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

    /**
     * @return return the number of tiles left in the class.
     * */
    protected int getRemaining() {
        return this.remaining;
    }

    /**
     * The function removes the Tile at position index
     * @param index index of extraction
     * @throws IllegalArgumentException If there is no Tile left at index i
     * */
    protected void pop(int index) {
        if (this.remainingTile[index] < 1) {
            throw new IllegalArgumentException("Index not valid");
        }

        this.remaining --;
        this.remainingTile[index] --;
    }

    /**
     * @param index index of interrogation
     * @return The number of remaining Tiles at index i.
     * */
    protected int getAvailable(int index) {
        return this.remainingTile[index];
    }

    public boolean isEmpty() {
        return this.getRemaining() == 0;
    }
}

/**
 * This represents the bag for a game, which contains all 6 types of {@link Tile tile} (22 tiles per type).
 * It's not possible to refill the bag
 * @author Giacomo Groppi
*/
public class Bag extends BagData {

    /**
     * Constructor of the class.
     * It initializes all 22*6 {@link Tile tile}
     * */
    public Bag() {
        super();
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
