package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

public class Fill extends FullyResizableDrawable {
    private final Symbol symbol;

    public Fill(Symbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        if (!size.isInside(coordinate)) {
            throw new OutOfDrawableException(size, coordinate);
        }

        return symbol;
    }

    @Override
    public boolean handleInput(String key) {
        return false;
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        return false;
    }

    @Override
    public void unfocus() {
        throw new IllegalStateException("You can't unfocus a fill element since it can't be focused");
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        return Optional.empty();
    }
}
