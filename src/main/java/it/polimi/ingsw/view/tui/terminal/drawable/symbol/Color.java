package it.polimi.ingsw.view.tui.terminal.drawable.symbol;

import java.util.List;

public enum Color {
    BLACK(List.of(30), List.of(40)),
    RED(List.of(31), List.of(41)),
    GREEN(List.of(32), List.of(42)),
    YELLOW(List.of(33), List.of(43)),
    BLUE(List.of(34), List.of(44)),
    MAGENTA(List.of(35), List.of(45)),
    CYAN(List.of(36), List.of(46)),
    WHITE(List.of(37), List.of(47)),
    GREY(236);

    private static List<Integer> craftForegroundDecorationsFromId(int id) {
        return List.of(38, 5, id);
    }

    private static List<Integer> craftBackgroundDecorationsFromId(int id) {
        return List.of(48, 5, id);
    }

    private final List<Integer> foregroundDecorations;
    private final List<Integer> backgroundDecorations;

    Color(List<Integer> foregroundDecorations, List<Integer> backgroundDecorations) {
        this.foregroundDecorations = foregroundDecorations;
        this.backgroundDecorations = backgroundDecorations;
    }

    Color(int id) {
        this.foregroundDecorations = craftForegroundDecorationsFromId(id);
        this.backgroundDecorations = craftBackgroundDecorationsFromId(id);
    }

    public List<Integer> colorForeground() {
        return foregroundDecorations;
    }

    public List<Integer> colorBackground() {
        return backgroundDecorations;
    }
}
