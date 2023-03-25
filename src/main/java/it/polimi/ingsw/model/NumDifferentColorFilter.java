package it.polimi.ingsw.model;

import java.util.Arrays;

/**
 * Allows to filter a sequence of tiles by the number of different colors in it.
 * The sequence must not contain empty tiles in order to satisfy the criteria.
 *
 * @author Cristiano Migali
 */
public class NumDifferentColorFilter implements Filter {
    /**
     * The minimum number of different colors that the sequence should contain.
     */
    private final int minColors;

    /**
     * The maximum number of different colors that the sequence should contain.
     */
    private final int maxColors;

    /**
     * The number of different colors inside the sequence before the insertion of the last added tile.
     */
    private int countBeforeLast;

    /**
     * The colors that were present inside the sequence before the addition of the last tile.
     * In particular a color (represented by a tile) was present in the sequence iff
     * {@code yetAddedBeforeLast[tile.ordinal()]} is true.
     */
    private final boolean[] yetAddedBeforeLast = new boolean[Tile.values().length];

    /**
     * The number of different colors inside the sequence.
     */
    private int count;

    /**
     * The colors that are present inside the sequence.
     * In particular a color (represented by a tile) is present in the sequence iff
     * {@code yetAdded[tile.ordinal()]} is true.
     */
    private final boolean[] yetAdded = new boolean[Tile.values().length];

    /**
     * Constructor of the class.
     * It initializes {@link NumDifferentColorFilter#minColors} and {@link NumDifferentColorFilter#maxColors}
     * arguments and sets the sequence to the empty sequence.
     *
     * @param minColors is the minimum number of different colors that a sequence should contain in order to satisfy
     *                  the criteria.
     * @param maxColors is the maximum number of different colors that a sequence should contain in order to satisfy
     *                  the criteria.
     */
    public NumDifferentColorFilter(int minColors, int maxColors) {
        this.minColors = minColors;
        this.maxColors = maxColors;

        clear();
    }

    @Override
    public boolean add(Tile tile) {
        countBeforeLast = count;
        for (Tile t : Tile.values()) {
            yetAddedBeforeLast[t.ordinal()] = yetAdded[t.ordinal()];
        }

        if (!yetAdded[tile.ordinal()]) {
            count++;
        }

        yetAdded[tile.ordinal()] = true;

        if (tile == Tile.EMPTY) {
            return true;
        }

        /*
         * Since the number of different colors inside the sequence can only grow by adding new tiles,
         * if we don't remove the last added tile, the criteria can't be satisfied.
         */
        return count > maxColors;
    }

    @Override
    public void forgetLastTile() {
        count = countBeforeLast;
        for (Tile t : Tile.values()) {
            yetAdded[t.ordinal()] = yetAddedBeforeLast[t.ordinal()];
        }
    }

    @Override
    public boolean isSatisfied() {
        return minColors <= count && count <= maxColors &&
            !yetAdded[Tile.EMPTY.ordinal()];
    }

    @Override
    public void clear() {
        countBeforeLast = 0;
        count = 0;
        Arrays.fill(yetAddedBeforeLast, false);
        Arrays.fill(yetAdded, false);
    }
}
