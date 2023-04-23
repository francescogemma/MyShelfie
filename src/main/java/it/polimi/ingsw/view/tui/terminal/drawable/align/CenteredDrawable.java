package it.polimi.ingsw.view.tui.terminal.drawable.align;

import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

public class CenteredDrawable extends AlignedDrawable {
    private final Drawable toCenter;
    private Coordinate toCenterOrigin;

    public CenteredDrawable(Drawable toCenter) {
        this.toCenter = toCenter;
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        toCenter.askForSize(desiredSize);

        int actualLines = Math.max(desiredSize.getLines(), toCenter.getSize().getLines());
        int actualColumns = Math.max(desiredSize.getColumns(), toCenter.getSize().getColumns());

        size = new DrawableSize(actualLines, actualColumns);

        int toCenterOriginLine = (actualLines - toCenter.getSize().getLines()) / 2 + 1;
        int toCenterOriginColumn = (actualColumns - toCenter.getSize().getColumns()) / 2 + 1;

        toCenterOrigin = new Coordinate(toCenterOriginLine, toCenterOriginColumn);
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        if (!size.isInside(coordinate)) {
            throw new OutOfDrawableException(size, coordinate);
        }

        if (coordinate.before(toCenterOrigin)) {
            return PrimitiveSymbol.EMPTY;
        }

        try {
            return toCenter.getSymbolAt(coordinate.changeOrigin(toCenterOrigin));
        } catch (OutOfDrawableException e) {
            return PrimitiveSymbol.EMPTY;
        }
    }

    @Override
    public boolean handleInput(String key) {
        return toCenter.handleInput(key);
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        int adjustedDesiredLine = desiredCoordinate.getLine();
        int adjustedDesiredColumn = desiredCoordinate.getColumn();

        if (adjustedDesiredLine < toCenterOrigin.getLine()) {
            adjustedDesiredLine = toCenterOrigin.getLine();
        }

        if (adjustedDesiredLine > toCenterOrigin.getLine() + Math.max(0, toCenter.getSize().getLines() - 1)) {
            adjustedDesiredLine = toCenterOrigin.getLine() + Math.max(0, toCenter.getSize().getLines() - 1);
        }

        if (adjustedDesiredColumn < toCenterOrigin.getColumn()) {
            adjustedDesiredColumn = toCenterOrigin.getColumn();
        }

        if (adjustedDesiredColumn > toCenterOrigin.getColumn() + Math.max(0, toCenter.getSize().getColumns() - 1)) {
            adjustedDesiredColumn = toCenterOrigin.getColumn() + Math.max(0, toCenter.getSize().getColumns() - 1);
        }

        return toCenter.focus(new Coordinate(adjustedDesiredLine, adjustedDesiredColumn).changeOrigin(toCenterOrigin));
    }

    @Override
    public void unfocus() {
        toCenter.unfocus();
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        return toCenter.getFocusedCoordinate().map(coordinate -> new Coordinate(
            coordinate.getLine() + toCenterOrigin.getLine() - 1,
            coordinate.getColumn() + toCenterOrigin.getColumn() - 1
        ));
    }
}