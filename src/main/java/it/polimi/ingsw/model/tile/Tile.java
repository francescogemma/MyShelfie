package it.polimi.ingsw.model.tile;

import java.util.Arrays;
import java.util.Collection;

/**
 * It represents a tile in the game.
 * Every tile is characterized by a color and a version.
 * For every color (except for the empty tile) there are 3 version, each one corresponds to a different foreground
 * image on the tile.
 * This class implements a singleton pattern, there is only one instance of Tile with a given color and version
 * couple.
 *
 * @author Cristiano Migali
 */
public class Tile {
    /**
     * It is the color of this tile.
     */
    private final TileColor color;

    /**
     * It is the version of this tile, it indicates the foreground image on the tile.
     */
    private final TileVersion version;

    /**
     * Constructor of the class.
     * It initializes the Tile color and version.
     *
     * @param tileColor is the color of the background of the tile.
     * @param tileVersion is the version which indicates the foreground image on the tile.
     */
    private Tile(TileColor tileColor, TileVersion tileVersion) {
        color = tileColor;
        version = tileVersion;
    }

    /**
     * Matrix used to store all the instances of Tile to implement a singleton pattern.
     */
    private static final Tile[][] INSTANCES = new Tile[TileColor.values().length][];

    static {
        for (TileColor tileColor : TileColor.values()) {
            if (tileColor == TileColor.EMPTY) {
                INSTANCES[tileColor.ordinal()] = new Tile[1];
                INSTANCES[tileColor.ordinal()][0] = new Tile(tileColor, TileVersion.FIRST);
                continue;
            }

            INSTANCES[tileColor.ordinal()] = new Tile[TileVersion.values().length];
            for (TileVersion tileVersion : TileVersion.values()) {
                INSTANCES[tileColor.ordinal()][tileVersion.ordinal()] = new Tile(tileColor, tileVersion);
            }
        }
    }

    /**
     * @return all the Tile instances in an array.
     *
     * @author Giacomo Groppi
     */
    public static Collection<Tile> getTiles() {
        return Arrays.stream(INSTANCES)
                .flatMap(Arrays::stream)
                .toList();
    }

    /**
     * Allows to retrieve the instance of the Tile with the specified color and version.
     * This is the only way of doing so, indeed this class implements a singleton pattern.
     *
     * @param tileColor is the background color of the tile.
     * @param tileVersion is the version which indicates the foreground image on the tile.
     * @return the instance of the tile with the specified color and version.
     *
     * @throws IllegalArgumentException if we try to retrieve the instance of the empty tile specifying
     * a version which is not the first.
     */
    public static Tile getInstance(TileColor tileColor, TileVersion tileVersion) {
        if (tileColor == TileColor.EMPTY) {
            if (tileVersion != TileVersion.FIRST) {
                throw new IllegalArgumentException("Empty tile has only one version");
            }

            return INSTANCES[TileColor.EMPTY.ordinal()][0];
        }

        return INSTANCES[tileColor.ordinal()][tileVersion.ordinal()];
    }

    /**
     * @return the background color of this tile.
     */
    public TileColor getColor() {
        return color;
    }

    /**
     * @return the version which indicates the foreground image on the tile.
     */
    public TileVersion getVersion() {
        return version;
    }

    /**
     * It is the number of different (accounting for versions) tiles which are not empty.
     */
    public static final int NUM_OF_DIFFERENT_NON_EMPTY_TILES = (TileColor.values().length - 1) *
        TileVersion.values().length;

    /**
     * Converts a tile which is not empty to a unique index.
     *
     * @param tile is the Tile that we want to convert to an index.
     * @return the index associated with the provided tile.
     */
    public static int nonEmptyTileToIndex(Tile tile) {
        if (tile.color == TileColor.EMPTY) {
            throw new IllegalArgumentException("Conversion of empty tile to index is not supported");
        }

        return (TileColor.tileColorToIndex(tile.color) - 1) * TileVersion.values().length +
            tile.version.ordinal();
    }

    /**
     * Converts an index to a non-empty tile.
     *
     * @param index is the index for which we want to retrieve the corresponding non-empty tile.
     * @return the Tile associated with the provided index.
     */
    public static Tile indexToNonEmptyTile(int index) {
        if (index < 0 || index >= NUM_OF_DIFFERENT_NON_EMPTY_TILES) {
            throw new IllegalArgumentException("index must be between 0 and " + (NUM_OF_DIFFERENT_NON_EMPTY_TILES - 1)
                + " when converting it to a non-empty tile, got: " + index);
        }

        int colorIndex = index / TileVersion.values().length + 1;

        return getInstance(TileColor.indexToTileColor(colorIndex),
            TileVersion.values()[index % TileVersion.values().length]);
    }

    @Override
    public String toString () {
        return "[" + this.color + this.version + "]";
    }
}
