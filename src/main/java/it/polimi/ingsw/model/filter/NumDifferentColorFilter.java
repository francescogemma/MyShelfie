package it.polimi.ingsw.model.filter;

import it.polimi.ingsw.model.tile.TileColor;

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
    private final boolean[] yetAddedBeforeLast = new boolean[TileColor.values().length];

    /**
     * The number of different colors inside the sequence.
     */
    private int count;

    /**
     * The colors that are present inside the sequence.
     * In particular a color (represented by a tile) is present in the sequence iff
     * {@code yetAdded[tile.ordinal()]} is true.
     */
    private final boolean[] yetAdded = new boolean[TileColor.values().length];

    /**
     * Constructor of the class.
     * It initializes {@link NumDifferentColorFilter#minColors} and {@link NumDifferentColorFilter#maxColors}
     * arguments and sets the sequence to the empty sequence.
     *
     * @param minColors is the minimum number of different colors that a sequence should contain in order to satisfy
     *                  the criteria.
     * @param maxColors is the maximum number of different colors that a sequence should contain in order to satisfy
     *                  the criteria.
     * @throws IllegalArgumentException if minColors is negative.
     * @throws IllegalArgumentException if maxColors isn't positive.
     */
    public NumDifferentColorFilter(int minColors, int maxColors) {
        if (minColors < 0) {
            throw new IllegalArgumentException("The minimum number of colors in a different color filter must be " +
                "non-negative");
        }

        if (maxColors <= 0) {
            throw new IllegalArgumentException("The maximum number of colors in a different color filter must be " +
                "positive");
        }

        this.minColors = minColors;
        this.maxColors = maxColors;

        clear();
    }

    @Override
    public boolean add(TileColor tileColor) {
        countBeforeLast = count;
        for (TileColor t : TileColor.values()) {
            yetAddedBeforeLast[t.ordinal()] = yetAdded[t.ordinal()];
        }

        if (!yetAdded[tileColor.ordinal()]) {
            count++;
        }

        yetAdded[tileColor.ordinal()] = true;

        if (tileColor == TileColor.EMPTY) {
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
        for (TileColor t : TileColor.values()) {
            yetAdded[t.ordinal()] = yetAddedBeforeLast[t.ordinal()];
        }
    }

    @Override
    public boolean isSatisfied() {
        return minColors <= count && count <= maxColors &&
            !yetAdded[TileColor.EMPTY.ordinal()];
    }

    @Override
    public void clear() {
        countBeforeLast = 0;
        count = 0;
        Arrays.fill(yetAddedBeforeLast, false);
        Arrays.fill(yetAdded, false);
    }
}
