package it.polimi.ingsw.view.tui.terminal.drawable.symbol;

public interface Symbol {
    String asString();
    PrimitiveSymbol getPrimitiveSymbol();

    DecoratedSymbol colorForeground(Color color);
    DecoratedSymbol colorBackground(Color color);
    DecoratedSymbol bold();
    DecoratedSymbol italic();
    DecoratedSymbol underline();
    DecoratedSymbol strikethrough();
}
