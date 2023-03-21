package it.polimi.ingsw.model;

public enum Tile {
    GREEN,
    WHITE,
    YELLOW,
    BLUE,
    CYAN,
    MAGENTA,
    EMPTY;

    public String color(String toColor) {
        return switch (this) {
            case GREEN -> "\033[30;42m" + toColor + "\033[0m";
            case WHITE -> "\033[30;47m" + toColor + "\033[0m";
            case YELLOW -> "\033[30;43m" + toColor + "\033[0m";
            case BLUE -> "\033[30;44m" + toColor + "\033[0m";
            case CYAN -> "\033[30;46m" + toColor + "\033[0m";
            case MAGENTA -> "\033[30;45m" + toColor + "\033[0m";
            case EMPTY -> toColor;
        };
    }
}
