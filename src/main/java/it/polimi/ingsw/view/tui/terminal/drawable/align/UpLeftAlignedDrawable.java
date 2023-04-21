package it.polimi.ingsw.view.tui.terminal.drawable.align;

import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

public class UpLeftAlignedDrawable extends AlignedDrawable {
    private final Drawable toAlignUpLeft;

    public UpLeftAlignedDrawable(Drawable toAlignUpLeft) {
        this.toAlignUpLeft = toAlignUpLeft;
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        toAlignUpLeft.askForSize(desiredSize);

        int actualLines = Math.max(desiredSize.getLines(), toAlignUpLeft.getSize().getLines());
        int actualColumns = Math.max(desiredSize.getColumns(), toAlignUpLeft.getSize().getColumns());

        size = new DrawableSize(actualLines, actualColumns);
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        if (!size.isInside(coordinate)) {
            throw new OutOfDrawableException(size, coordinate);
        }

        try {
            return toAlignUpLeft.getSymbolAt(coordinate);
        } catch (OutOfDrawableException e) {
            return PrimitiveSymbol.EMPTY;
        }
    }

    @Override
    public boolean handleInput(String key) {
        return toAlignUpLeft.handleInput(key);
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        int adjustedDesiredLine = desiredCoordinate.getLine();
        int adjustedDesiredColumn = desiredCoordinate.getColumn();

        if (adjustedDesiredLine > Math.max(size.getLines(), 1)) {
            adjustedDesiredLine = Math.max(size.getLines(), 1);
        }

        if (adjustedDesiredColumn > Math.max(size.getColumns(), 1)) {
            adjustedDesiredColumn = Math.max(size.getColumns(), 1);
        }

        return toAlignUpLeft.focus(new Coordinate(adjustedDesiredLine, adjustedDesiredColumn));
    }

    @Override
    public void unfocus() {
        toAlignUpLeft.unfocus();
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        return toAlignUpLeft.getFocusedCoordinate();
    }
}
