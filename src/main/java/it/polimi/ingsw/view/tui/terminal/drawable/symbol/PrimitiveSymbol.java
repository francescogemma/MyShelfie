package it.polimi.ingsw.view.tui.terminal.drawable.symbol;

import java.util.List;

/**
 * Represents a {@link Symbol} without formatting, that is only a character which can be printed to the terminal.
 * Every {@link Symbol} is obtained by eventually adding formatting (background color, foreground color, bold effect, ...)
 * to a PrimitiveSymbol.
 *
 * @see DecoratedSymbol
 *
 * @author Cristiano Migali
 */
public enum PrimitiveSymbol implements Symbol {
    /**
     * It represents the "┌" character.
     */
    UPPER_LEFT_BOX_BORDER("┌"),

    /**
     * It represents the "┐" character.
     */
    UPPER_RIGHT_BOX_BORDER("┐"),

    /**
     * It represents the "└" character.
     */
    LOWER_LEFT_BOX_BORDER("└"),

    /**
     * It represents the "┘" character.
     */
    LOWER_RIGHT_BOX_BORDER("┘"),

    /**
     * It represents the "│" character.
     */
    VERTICAL_BOX_BORDER("│"),

    /**
     * It represents the "─" character.
     */
    HORIZONTAL_BOX_BORDER("─"),

    /**
     * It represents the "├" character.
     */
    T_RIGHT("├"),

    /**
     * It represents the "┤" character.
     */
    T_LEFT("┤"),

    /**
     * It represents the "┼" character.
     */
    CROSS("┼"),

    /**
     * It represents the "\" character.
     */
    MAIN_DIAGONAL("\\"),

    /**
     * It represents the "/" character.
     */
    SECONDARY_DIAGONAL("/"),

    /**
     * It represents the "*" character.
     */
    STAR("*"),

    /**
     * It represents the "+" character.
     */
    PLUS("+"),

    /**
     * It represents the "#" character.
     */
    HASHTAG("#"),

    /**
     * It represents the "a" character.
     */
    SMALL_A("a"),

    /**
     * It represents the "b" character.
     */
    SMALL_B("b"),

    /**
     * It represents the "c" character.
     */
    SMALL_C("c"),

    /**
     * It represents the "d" character.
     */
    SMALL_D("d"),

    /**
     * It represents the "e" character.
     */
    SMALL_E("e"),

    /**
     * It represents the "f" character.
     */
    SMALL_F("f"),

    /**
     * It represents the "g" character.
     */
    SMALL_G("g"),

    /**
     * It represents the "h" character.
     */
    SMALL_H("h"),

    /**
     * It represents the "i" character.
     */
    SMALL_I("i"),

    /**
     * It represents the "j" character.
     */
    SMALL_J("j"),

    /**
     * It represents the "k" character.
     */
    SMALL_K("k"),

    /**
     * It represents the "l" character.
     */
    SMALL_L("l"),

    /**
     * It represents the "m" character.
     */
    SMALL_M("m"),

    /**
     * It represents the "n" character.
     */
    SMALL_N("n"),

    /**
     * It represents the "o" character.
     */
    SMALL_O("o"),

    /**
     * It represents the "p" character.
     */
    SMALL_P("p"),

    /**
     * It represents the "q" character.
     */
    SMALL_Q("q"),

    /**
     * It represents the "r" character.
     */
    SMALL_R("r"),

    /**
     * It represents the "s" character.
     */
    SMALL_S("s"),

    /**
     * It represents the "t" character.
     */
    SMALL_T("t"),

    /**
     * It represents the "u" character.
     */
    SMALL_U("u"),

    /**
     * It represents the "v" character.
     */
    SMALL_V("v"),

    /**
     * It represents the "w" character.
     */
    SMALL_W("w"),

    /**
     * It represents the "x" character.
     */
    SMALL_X("x"),

    /**
     * It represents the "y" character.
     */
    SMALL_Y("y"),

    /**
     * It represents the "z" character.
     */
    SMALL_Z("z"),

    /**
     * It represents the "A" character.
     */
    BIG_A("A"),

    /**
     * It represents the "B" character.
     */
    BIG_B("B"),

    /**
     * It represents the "C" character.
     */
    BIG_C("C"),

    /**
     * It represents the "D" character.
     */
    BIG_D("D"),

    /**
     * It represents the "E" character.
     */
    BIG_E("E"),

    /**
     * It represents the "F" character.
     */
    BIG_F("F"),

    /**
     * It represents the "G" character.
     */
    BIG_G("G"),

    /**
     * It represents the "H" character.
     */
    BIG_H("H"),

    /**
     * It represents the "I" character.
     */
    BIG_I("I"),

    /**
     * It represents the "J" character.
     */
    BIG_J("J"),

    /**
     * It represents the "K" character.
     */
    BIG_K("K"),

    /**
     * It represents the "L" character.
     */
    BIG_L("L"),

    /**
     * It represents the "M" character.
     */
    BIG_M("M"),

    /**
     * It represents the "N" character.
     */
    BIG_N("N"),

    /**
     * It represents the "O" character.
     */
    BIG_O("O"),

    /**
     * It represents the "P" character.
     */
    BIG_P("P"),

    /**
     * It represents the "Q" character.
     */
    BIG_Q("Q"),

    /**
     * It represents the "R" character.
     */
    BIG_R("R"),

    /**
     * It represents the "S" character.
     */
    BIG_S("S"),

    /**
     * It represents the "T" character.
     */
    BIG_T("T"),

    /**
     * It represents the "U" character.
     */
    BIG_U("U"),

    /**
     * It represents the "V" character.
     */
    BIG_V("V"),

    /**
     * It represents the "W" character.
     */
    BIG_W("W"),

    /**
     * It represents the "X" character.
     */
    BIG_X("X"),

    /**
     * It represents the "Y" character.
     */
    BIG_Y("Y"),

    /**
     * It represents the "Z" character.
     */
    BIG_Z("Z"),

    /**
     * It represents the "_" character.
     */
    UNDERSCORE("_"),

    /**
     * It represents the "-" character.
     */
    HYPHEN("-"),

    /**
     * It represents the "0" character.
     */
    ZERO("0"),

    /**
     * It represents the "1" character.
     */
    ONE("1"),

    /**
     * It represents the "2" character.
     */
    TWO("2"),

    /**
     * It represents the "3" character.
     */
    THREE("3"),

    /**
     * It represents the "4" character.
     */
    FOUR("4"),

    /**
     * It represents the "5" character.
     */
    FIVE("5"),

    /**
     * It represents the "6" character.
     */
    SIX("6"),

    /**
     * It represents the "7" character.
     */
    SEVEN("7"),

    /**
     * It represents the "8" character.
     */
    EIGHT("8"),

    /**
     * It represents the "9" character.
     */
    NINE("9"),

    /**
     * It represents the "{@literal <}" character.
     */
    LESS_THAN("<"),

    /**
     * It represents the ">" character.
     */
    GREATER_THAN(">"),

    /**
     * It represents the ":" character.
     */
    COLON(":"),

    /**
     * It represents the "," character.
     */
    COMMA(","),

    /**
     * It represents the "(" character.
     */
    LEFT_PARENTHESIS("("),

    /**
     * It represents the ")" character.
     */
    RIGHT_PARENTHESIS(")"),

    /**
     * It represents the "!" character.
     */
    EXCLAMATION_MARK("!"),

    /**
     * It represents the "." character.
     */
    POINT("."),

    /**
     * It represents the "'" character.
     */
    APOSTROPHE("'"),

    /**
     * It represents the "[" character.
     */
    OPEN_SQUARE_BRACKET("["),

    /**
     * It represents the "]" character.
     */
    CLOSED_SQUARE_BRACKET("]"),

    /**
     * It represents the " " character.
     */
    EMPTY(" ");

    /**
     * It is the character represented by this primitive symbol.
     */
    private final String symbol;

    /**
     * Constructor of the class.
     * Initializes the character corresponding to the primitive symbol.
     *
     * @param symbol is the character represented by this PrimitiveSymbol
     */
    PrimitiveSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String asString() {
        return symbol;
    }

    /**
     * @param s is a string containing a single character corresponding to the PrimitiveSymbol that we want to
     *          retrieve.
     * @return a PrimitiveSymbol representing the character specified through s.
     */
    public static PrimitiveSymbol fromString(String s) {
        for (PrimitiveSymbol symbol : values()) {
            if (symbol.asString().equals(s)) {
                return symbol;
            }
        }

        throw new IllegalArgumentException("There is no primitive symbol corresponding to: " + s);
    }

    @Override
    public DecoratedSymbol colorForeground(Color color) {
        return DecoratedSymbol.getInstance(this, color.colorForeground());
    }

    @Override
    public DecoratedSymbol colorBackground(Color color) {
        return DecoratedSymbol.getInstance(this, color.colorBackground());
    }

    @Override
    public DecoratedSymbol bold() {
        return DecoratedSymbol.getInstance(this, List.of(1));
    }

    @Override
    public DecoratedSymbol italic() {
        return DecoratedSymbol.getInstance(this, List.of(3));
    }

    @Override
    public DecoratedSymbol underline() {
        return DecoratedSymbol.getInstance(this, List.of(4));
    }

    @Override
    public DecoratedSymbol strikethrough() {
        return DecoratedSymbol.getInstance(this, List.of(9));
    }

    /**
     * @param color is the color which we want to format the foreground of the PrimitiveSymbol with.
     * @return a new {@link Symbol} which is obtained by making this PrimitiveSymbol bold and coloring its
     * foreground with the specified color.
     */
    public Symbol highlight(Color color) {
        return colorForeground(color).bold();
    }

    /**
     * @param color is the color which we want to format the foreground of the PrimitiveSymbol with.
     * @param toHighlight must be true iff we want to highlight the current PrimitiveSymbol.
     * @return a new {@link Symbol} which is obtained by making this PrimitiveSymbol bold and coloring its foreground
     * with the specified color iff toHighlight is true, the current PrimitiveSymbol otherwise.
     */
    public Symbol highlight(Color color, boolean toHighlight) {
        if (toHighlight) {
            return highlight(color);
        }

        return this;
    }

    /**
     * @return a new {@link Symbol} which is obtained by formatting the current PrimitiveSymbol with white
     * background and black foreground.
     */
    public Symbol highlightBackground() {
        return colorBackground(Color.WHITE).colorForeground(Color.BLACK);
    }

    /**
     * @param toHighlightBackground must be true iff we want to highlight the background of the current PrimitiveSymbol.
     * @return a new {@link Symbol} which is obtained by formatting the current PrimitiveSymbol with white background
     * and black foreground iff toHighlightBackground is true, the current PrimitiveSymbol otherwise.
     */
    public Symbol highlightBackground(boolean toHighlightBackground) {
        if (toHighlightBackground) {
            return highlightBackground();
        }

        return this;
    }

    /**
     * @return a new {@link Symbol} which is obtained by formatting the current PrimitiveSymbol with grey foreground.
     */
    public Symbol blur() {
        return colorForeground(Color.GREY);
    }

    /**
     * @param toBlur must be true iff we want ot blur the current PrimitiveSymbol.
     * @return a new {@link Symbol} which is obtained by formatting the current PrimitiveSymbol with grey foreground,
     * the current PrimitiveSymbol otherwise.
     */
    public Symbol blur(boolean toBlur) {
        if (toBlur) {
            return blur();
        }

        return this;
    }

    @Override
    public PrimitiveSymbol getPrimitiveSymbol() {
        return this;
    }
}
