package it.polimi.ingsw.model;

/**
 * Represents the type of the game tiles. They can be inserted inside a {@link Library library},
 * placed on the {@link Board board} or remain inside the {@link Bag bag}.
 *
 * @author Giacomo Groppi, Cristiano Migali
 */
public enum Tile {
    GREEN("green", "\033[30;42m"),
    WHITE("white", "\033[30;47m"),
    YELLOW("yellow", "\033[30;43m"),
    BLUE("blue", "\033[30;44m"),
    CYAN("cyan", "\033[30;46m"),
    MAGENTA("magenta", "\033[30;45m"),
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
    Tile(String name, String color) {
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
}
