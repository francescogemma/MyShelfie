package it.polimi.ingsw.model;

public enum Tile {
    GREEN("green", "\033[30;42m"),
    WHITE("white", "\033[30;47m"),
    YELLOW("yellow", "\033[30;43m"),
    BLUE("blue", "\033[30;44m"),
    CYAN("cyan", "\033[30;46m"),
    MAGENTA("magenta", "\033[30;45m"),
    EMPTY("empty", "");

    private final String name;
    private final String colorCode;

    private Tile(String name, String color) {
        this.colorCode = color;
        this.name = name;
    }

    public String color(String toColor) {
        return this.colorCode + toColor + "\033[0m";
    }

    @Override
    public String toString() {
        return this.name;
    }
}
