package it.polimi.ingsw.view.tui.terminal.drawable.symbol;

import java.util.List;

public enum PrimitiveSymbol implements Symbol {
    UPPER_LEFT_BOX_BORDER("┌"),
    UPPER_RIGHT_BOX_BORDER("┐"),
    LOWER_LEFT_BOX_BORDER("└"),
    LOWER_RIGHT_BOX_BORDER("┘"),
    VERTICAL_BOX_BORDER("│"),
    HORIZONTAL_BOX_BORDER("─"),
    T_RIGHT("├"),
    T_LEFT("┤"),
    STAR("*"),
    PLUS("+"),
    HASHTAG("#"),
    SMALL_A("a"),
    SMALL_B("b"),
    SMALL_C("c"),
    SMALL_D("d"),
    SMALL_E("e"),
    SMALL_F("f"),
    SMALL_G("g"),
    SMALL_H("h"),
    SMALL_I("i"),
    SMALL_J("j"),
    SMALL_K("k"),
    SMALL_L("l"),
    SMALL_M("m"),
    SMALL_N("n"),
    SMALL_O("o"),
    SMALL_P("p"),
    SMALL_Q("q"),
    SMALL_R("r"),
    SMALL_S("s"),
    SMALL_T("t"),
    SMALL_U("u"),
    SMALL_V("v"),
    SMALL_W("w"),
    SMALL_X("x"),
    SMALL_Y("y"),
    SMALL_Z("z"),
    BIG_A("A"),
    BIG_B("B"),
    BIG_C("C"),
    BIG_D("D"),
    BIG_E("E"),
    BIG_F("F"),
    BIG_G("G"),
    BIG_H("H"),
    BIG_I("I"),
    BIG_J("J"),
    BIG_K("K"),
    BIG_L("L"),
    BIG_M("M"),
    BIG_N("N"),
    BIG_O("O"),
    BIG_P("P"),
    BIG_Q("Q"),
    BIG_R("R"),
    BIG_S("S"),
    BIG_T("T"),
    BIG_U("U"),
    BIG_V("V"),
    BIG_W("W"),
    BIG_X("X"),
    BIG_Y("Y"),
    BIG_Z("Z"),
    UNDERSCORE("_"),
    HYPHEN("-"),
    ZERO("0"),
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    COLON(":"),
    COMMA(","),
    LEFT_PARENTHESIS("("),
    RIGHT_PARENTHESIS(")"),
    EXCLAMATION_MARK("!"),
    POINT("."),
    APOSTROPHE("'"),
    EMPTY(" ");

    private final String symbol;

    PrimitiveSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String asString() {
        return symbol;
    }

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

    public Symbol highlight(Color color) {
        return colorForeground(color).bold();
    }

    public Symbol highlight(Color color, boolean toHighlight) {
        if (toHighlight) {
            return highlight(color);
        }

        return this;
    }

    public Symbol highlightBackground() {
        return colorBackground(Color.WHITE).colorForeground(Color.BLACK);
    }

    public Symbol highlightBackground(boolean toHighlightBackground) {
        if (toHighlightBackground) {
            return highlightBackground();
        }

        return this;
    }

    public Symbol blur() {
        return colorForeground(Color.GREY);
    }

    @Override
    public PrimitiveSymbol getPrimitiveSymbol() {
        return this;
    }
}
