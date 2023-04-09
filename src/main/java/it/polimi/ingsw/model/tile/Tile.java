package it.polimi.ingsw.model.tile;

public class Tile {
    private final TileColor color;
    private final TileVersion version;

    private Tile(TileColor tileColor, TileVersion tileVersion) {
        color = tileColor;
        version = tileVersion;
    }

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

    public static Tile getInstance(TileColor tileColor, TileVersion tileVersion) {
        if (tileColor == TileColor.EMPTY) {
            if (tileVersion != TileVersion.FIRST) {
                throw new IllegalArgumentException("Empty tile has only one version");
            }

            return INSTANCES[TileColor.EMPTY.ordinal()][0];
        }

        return INSTANCES[tileColor.ordinal()][tileVersion.ordinal()];
    }

    public TileColor getColor() {
        return color;
    }

    public TileVersion getVersion() {
        return version;
    }

    public static final int NUM_OF_DIFFERENT_NON_EMPTY_TILES = (TileColor.values().length - 1) *
        TileVersion.values().length;

    public static int nonEmptyTileToIndex(Tile tile) {
        if (tile.color == TileColor.EMPTY) {
            throw new IllegalArgumentException("Conversion of empty tile to index is not supported");
        }

        return (TileColor.tileColorToIndex(tile.color) - 1) * TileVersion.values().length +
            tile.version.ordinal();
    }

    public static Tile indexToNonEmptyTile(int index) {
        if (index < 0 || index >= NUM_OF_DIFFERENT_NON_EMPTY_TILES) {
            throw new IllegalArgumentException("index must be between 0 and " + (NUM_OF_DIFFERENT_NON_EMPTY_TILES - 1)
                + " when converting it to a non-empty tile, got: " + index);
        }

        int colorIndex = index / TileVersion.values().length + 1;

        return getInstance(TileColor.indexToTileColor(colorIndex),
            TileVersion.values()[index % TileVersion.values().length]);
    }
}
