package it.polimi.ingsw.view.tui.terminal.drawable.symbol;

import java.util.List;

/**
 * Represents the color of the text or background of an area in the terminal.
 * It allows to retrieve the list of integers to be used to craft an ANSI control sequence with the following syntax:
 * ESC[integer1;integer2;...m which can be printed to color the foreground or the background of a certain area in the
 * terminal with the represented color.
 * In particular Colors are employed to craft {@link DecoratedSymbol} with a certain text or
 * background color from a {@link PrimitiveSymbol}.
 *
 * @author Cristiano Migali
 */
public enum Color {
    /**
     * It represents the black color.
     */
    BLACK(List.of(30), List.of(40)),

    /**
     * It represents the default ANSI red color.
     */
    RED(List.of(31), List.of(41)),

    /**
     * It represents the default ANSI green color.
     */
    GREEN(List.of(32), List.of(42)),

    /**
     * It represents the default ANSI yellow color.
     */
    YELLOW(List.of(33), List.of(43)),

    /**
     * It represents the default ANSI blue color.
     */
    BLUE(List.of(34), List.of(44)),

    /**
     * It represents the default ANSI magenta color.
     */
    MAGENTA(List.of(35), List.of(45)),

    /**
     * It represents the default ANSI cyan color.
     */
    CYAN(List.of(36), List.of(46)),

    /**
     * It represents the white color.
     */
    WHITE(List.of(37), List.of(47)),

    /**
     * It represents the color with ANSI is 236, which corresponds to a shade of grey.
     */
    GREY(236);

    /**
     * It is the default color for the border boxes of focused areas on the terminal.
     */
    public static final Color FOCUS = RED;

    /**
     * @param id is the <a href="https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797#256-colors">ANSI ID</a>
     *           of the desired color.
     * @return a list of integers to be used inside an ANSI escape sequence with the following syntax:
     * ESC[integer1;integer2;...m to set the foreground color of a certain area
     * in the terminal to the one specified through id.
     */
    private static List<Integer> craftForegroundDecorationsFromId(int id) {
        return List.of(38, 5, id);
    }

    /**
     * @param id is the <a href="https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797#256-colors">ANSI ID</a>
     *           of the desired color.
     * @return a list of integers to be used inside an ANSI escape sequence with the following syntax:
     * ESC[integer1;integer2;...m to set the background color of a certain
     * area in the terminal to the one specified through id.
     */
    private static List<Integer> craftBackgroundDecorationsFromId(int id) {
        return List.of(48, 5, id);
    }

    /**
     * Is a list of integers to be used inside an ANSI escape sequence with the following syntax:
     * ESC[integer1;integer2;...m to set the foreground color of a certain area
     * in the terminal.
     */
    private final List<Integer> foregroundDecorations;

    /**
     * Is a list of integers to be used inside an ANSI escape sequence with the following syntax:
     * ESC[integer1;integer2;...m to set the background color of a certain area in
     * the terminal.
     */
    private final List<Integer> backgroundDecorations;

    /**
     * Constructor of the class.
     * It initializes the lists of integers to be used in ANSI escape sequences with the following syntax:
     * ESC[integer1;integer2;...m to set the foreground or background
     * of a certain area of the terminal to the represented color.
     *
     * @param foregroundDecorations is the list of integers to be used inside the ANSI escape sequence for the
     *                              foreground color.
     * @param backgroundDecorations is the list of integers to be used inside the ANSI escape sequence for the
     *                              background color.
     */
    Color(List<Integer> foregroundDecorations, List<Integer> backgroundDecorations) {
        this.foregroundDecorations = foregroundDecorations;
        this.backgroundDecorations = backgroundDecorations;
    }

    /**
     * Constructor of the class.
     * It initializes the list of integers to be used in ANSI escape sequences with the following syntax:
     * ESC[integer1;integer2;...m to set the foreground or background
     * of a certain area of the terminal to the color specified by id, which is an ANSI ID.
     *
     * @param id is the <a href="https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797#256-colors">ANSI ID</a>
     *           for the represented color.
     */
    Color(int id) {
        this.foregroundDecorations = craftForegroundDecorationsFromId(id);
        this.backgroundDecorations = craftBackgroundDecorationsFromId(id);
    }

    /**
     * @return the list of integers to be used inside an ANSI escape sequence with the following syntax:
     * ESC[integer1;integer2;...m to color the foreground of a terminal
     * area with the represented color.
     */
    public List<Integer> colorForeground() {
        return foregroundDecorations;
    }

    /**
     * @return the list of integers to be used inside an ANSI escape sequence with the following syntax:
     * ESC[integer1;integer2;...m to color the background of a terminal
     * area with the represented color.
     */
    public List<Integer> colorBackground() {
        return backgroundDecorations;
    }
}
