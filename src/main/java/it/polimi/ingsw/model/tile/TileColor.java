package it.polimi.ingsw.model.tile;

import it.polimi.ingsw.model.bag.Bag;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.bookshelf.Bookshelf;

/**
 * Represents the type of the game tiles. They can be inserted inside a {@link Bookshelf bookshelf},
 * placed on the {@link Board board} or remain inside the {@link Bag bag}.
 *
 * @author Giacomo Groppi, Cristiano Migali
 */
public enum TileColor {
    /**
     * It is the green color.
     */
    GREEN("green", "\033[30;42m"),

    /**
     * It is the white color.
     */
    WHITE("white", "\033[30;47m"),

    /**
     * It is the yellow color.
     */
    YELLOW("yellow", "\033[30;43m"),

    /**
     * It is the blue color.
     */
    BLUE("blue", "\033[30;44m"),

    /**
     * It is the cyan color.
     */
    CYAN("cyan", "\033[30;46m"),

    /**
     * It is the magenta color.
     */
    MAGENTA("magenta", "\033[30;45m"),

    /**
     * It represents a cell where there is no tile.
     */
    EMPTY("empty", "");

    /**
     * The name of the type of the tile. It can be the name of a color or "empty".
     */
    private final String name;

    /**
     * The ANSI escape sequence that allows to color a string with the color represented by the tile.
     */
    private final String colorCode;

    /**
     * Constructor of the class
     *
     * @param name is the name of the type of the tile.
     * @param color is the ANSI escape sequence that allows to color a string with the color represented by the tile.
     */
    TileColor(String name, String color) {
        this.colorCode = color;
        this.name = name;
    }

    /**
     * @param toColor is the string that we want to color.
     * @return the string toColor surrounded by ANSI escape sequences that colors it with the color of the tile on which
     * this method has been invoked.
     */
    public String color(String toColor) {
        return this.colorCode + toColor + "\033[0m";
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Converts an index to a TileColor.
     *
     * @param index is the index which we want to convert to the corresponding TileColor.
     * @return the TileColor corresponding to the index.
     */
    public static TileColor indexToTileColor(int index) {
        if (index == 0) {
            return TileColor.EMPTY;
        }

        TileColor tileColor = TileColor.EMPTY;
        for (TileColor t : TileColor.values()) {
            if (t != TileColor.EMPTY) {
                tileColor = t;
                index--;

                if (index == 0) {
                    break;
                }
            }
        }

        if (tileColor == TileColor.EMPTY) {
            throw new IllegalStateException("Result of indexToTile must be empty iff index is 0");
        }

        return tileColor;
    }

    /**
     * Converts a TileColor to an index.
     *
     * @param tileColor is the TileColor which we want to convert to the corresponding index.
     * @return index corresponding to the TileColor.
     */
    public static int tileColorToIndex(TileColor tileColor) {
        if (tileColor == TileColor.EMPTY) {
            return 0;
        }

        int index = 1;
        for (TileColor t : TileColor.values()) {
            if (t != TileColor.EMPTY) {
                if (t == tileColor) {
                    return index;
                }

                index++;
            }
        }

        throw new IllegalStateException("We must find tile in Tile.values when converting a tile to an index");
    }
}
