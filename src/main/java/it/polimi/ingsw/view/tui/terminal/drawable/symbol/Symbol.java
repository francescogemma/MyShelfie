package it.polimi.ingsw.view.tui.terminal.drawable.symbol;

/**
 * Represents a symbol that can be printed in a single cell of the terminal.
 * Every class that inherits from Symbol must implement a Singleton pattern: two Symbols are different
 * iff they are stored at different memory locations.
 * Two Symbols to be equal need not only to represent the same character, but also they must have the same
 * formatting: background color, foreground color, bold effect, ... .
 *
 * @see PrimitiveSymbol
 * @see DecoratedSymbol
 *
 * @author Cristiano Migali
 */
public interface Symbol {
    /**
     * @return a string which can be printed to the terminal to display the represented Symbol, with all its
     * formatting (background color, foreground color, bold effect, ...).
     */
    String asString();

    /**
     * @return the {@link PrimitiveSymbol} from which this Symbol has been crafted eventually by adding
     * decorations (background color, foreground color, bold effect, ...).
     *
     * @see PrimitiveSymbol
     */
    PrimitiveSymbol getPrimitiveSymbol();

    /**
     * @param color is the {@link Color} with which we want to format the foreground of this Symbol.
     *
     * @return a new {@link DecoratedSymbol} which is obtained by coloring the foreground of the current
     * Symbol with the specified {@link Color}.
     */
    DecoratedSymbol colorForeground(Color color);

    /**
     * @param color is the {@link Color} with which we want to format the background of this Symbol.
     *
     * @return a new {@link DecoratedSymbol} which is obtained by coloring the background of the current
     * Symbol with the specified {@link Color}.
     */
    DecoratedSymbol colorBackground(Color color);

    /**
     * @return a new {@link DecoratedSymbol} which is obtained by adding bold formatting to the current
     * Symbol.
     */
    DecoratedSymbol bold();

    /**
     * @return a new {@link DecoratedSymbol} which is obtained by adding italic formatting to the current Symbol.
     */
    DecoratedSymbol italic();

    /**
     * @return a new {@link DecoratedSymbol} which is obtained by adding underline formatting to the current Symbol.
     */
    DecoratedSymbol underline();

    /**
     * @return a new {@link DecoratedSymbol} which is obtained by adding strikethrough formatting to the current Symbol.
     */
    DecoratedSymbol strikethrough();
}
