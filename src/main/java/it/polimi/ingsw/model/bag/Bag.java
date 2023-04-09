package it.polimi.ingsw.model.bag;
import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.tile.TileVersion;

import java.util.Arrays;
import java.util.Random;

/**
 * This represents the bag for a game, which contains all 6 types of {@link TileColor tile} (22 tiles per type).
 * It's not possible to refill the bag
 * @author Giacomo Groppi
*/
public class Bag {
    /**
     * Specifies how different versions of the same tile are distributed in the bag.
     */
    private static final int[] VERSIONS_DISTRIBUTION = new int[Tile.NUM_OF_DIFFERENT_NON_EMPTY_TILES];

    private static void setVersionsDistribution(TileColor tileColor, TileVersion tileVersion, int num) {
        VERSIONS_DISTRIBUTION[Tile.nonEmptyTileToIndex(Tile.getInstance(tileColor, tileVersion))] = num;
    }

    private static int getVersionsDistribution(TileColor tileColor, TileVersion tileVersion) {
        return VERSIONS_DISTRIBUTION[Tile.nonEmptyTileToIndex(Tile.getInstance(tileColor, tileVersion))];
    }

    static  {
        setVersionsDistribution(TileColor.GREEN, TileVersion.FIRST, 8);
        setVersionsDistribution(TileColor.GREEN, TileVersion.SECOND, 7);
        setVersionsDistribution(TileColor.GREEN, TileVersion.THIRD, 7);

        setVersionsDistribution(TileColor.WHITE, TileVersion.FIRST, 8);
        setVersionsDistribution(TileColor.WHITE, TileVersion.SECOND, 7);
        setVersionsDistribution(TileColor.WHITE, TileVersion.THIRD, 7);

        setVersionsDistribution(TileColor.YELLOW, TileVersion.FIRST, 8);
        setVersionsDistribution(TileColor.YELLOW, TileVersion.SECOND, 7);
        setVersionsDistribution(TileColor.YELLOW, TileVersion.THIRD, 7);

        setVersionsDistribution(TileColor.BLUE, TileVersion.FIRST, 8);
        setVersionsDistribution(TileColor.BLUE, TileVersion.SECOND, 7);
        setVersionsDistribution(TileColor.BLUE, TileVersion.THIRD, 7);

        setVersionsDistribution(TileColor.CYAN, TileVersion.FIRST, 8);
        setVersionsDistribution(TileColor.CYAN, TileVersion.SECOND, 7);
        setVersionsDistribution(TileColor.CYAN, TileVersion.THIRD, 7);

        setVersionsDistribution(TileColor.MAGENTA, TileVersion.FIRST, 8);
        setVersionsDistribution(TileColor.MAGENTA, TileVersion.SECOND, 7);
        setVersionsDistribution(TileColor.MAGENTA, TileVersion.THIRD, 7);
    }

    private int remaining;
    private final int[] remainingTiles = new int[Tile.NUM_OF_DIFFERENT_NON_EMPTY_TILES];
    private int lastExtraction;

    /**
     * Constructor of the class.
     * It initializes all 22*6 {@link TileColor tile}
     * */
    public Bag() {
        for (TileColor tileColor : TileColor.values()) {
            if (tileColor != TileColor.EMPTY) {
                for (TileVersion tileVersion : TileVersion.values()) {
                    remainingTiles[Tile.nonEmptyTileToIndex(Tile.getInstance(tileColor, tileVersion))] =
                        getVersionsDistribution(tileColor, tileVersion);
                }
            }
        }

        lastExtraction = -1;

        remaining = 0;
        for (int num : VERSIONS_DISTRIBUTION) {
            remaining += num;
        }
    }

    public Bag (final Bag bag) {
        for (int i = 0; i < bag.remainingTiles.length; i++) {
            this.remainingTiles[i] = bag.remainingTiles[i];
        }

        this.remaining = bag.remaining;
        this.lastExtraction = bag.lastExtraction;
    }

    /**
     * The function selects a random {@link TileColor tile} from the bag and removes it.
     * @return a random {@link TileColor tile}
     * @see TileColor
     * */
    public Tile getRandomTile() {
        if (this.isEmpty()) {
            throw new IllegalStateException("Bag is empty");
        }

        final int index = new Random().nextInt(Tile.NUM_OF_DIFFERENT_NON_EMPTY_TILES);

        for (int i = 0; i < Tile.NUM_OF_DIFFERENT_NON_EMPTY_TILES; i++) {
            final int realIndex = (index + i) % Tile.NUM_OF_DIFFERENT_NON_EMPTY_TILES;
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
        if (this.remainingTiles[index] < 1) {
            throw new IllegalArgumentException("Index not valid");
        }

        this.remaining --;
        this.remainingTiles[index] --;
        lastExtraction = index;
        return Tile.indexToNonEmptyTile(index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return Arrays.equals(this.remainingTiles, ((Bag) o).remainingTiles);
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
        this.remainingTiles[this.lastExtraction] ++;
        this.lastExtraction = -1;
    }

    /**
     * @param index index of interrogation
     * @return The number of remaining Tiles at index i.
     * */
    protected int getAvailable(int index) {
        return this.remainingTiles[index];
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
