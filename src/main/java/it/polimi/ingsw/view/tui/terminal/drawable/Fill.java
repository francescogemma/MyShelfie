package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

/**
 * It is a {@link FullyResizableDrawable} which fills all the available space with a given {@link Symbol}.
 *
 * @author Cristiano Migali
 */
public class Fill extends FullyResizableDrawable {
    /**
     * It is the symbol used to fill all the available space.
     */
    private final Symbol symbol;

    /**
     * Constructor of the class.
     * It initializes the symbol used to fill all the available space.
     *
     * @param symbol is the {@link Symbol} which is used to fill all the available space assigned to the Drawable.
     */
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
