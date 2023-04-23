package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

public class WithBorderBoxDrawable extends Drawable {
    private final Drawable toAddBorderBox;

    private boolean onFocus = false;

    public WithBorderBoxDrawable(Drawable toAddBorderBox) {
        this.toAddBorderBox = toAddBorderBox;
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        toAddBorderBox.askForSize(new DrawableSize(
            Math.max(desiredSize.getLines() - 2, 0),
            Math.max(desiredSize.getColumns() - 2, 0)
        ));

        size = new DrawableSize(
            toAddBorderBox.getSize().getLines() + 2,
            toAddBorderBox.getSize().getColumns() + 2
        );
    }

    public static Optional<PrimitiveSymbol> addBorder(Coordinate coordinate, DrawableSize size) {
        if (!size.isInside(coordinate)) {
            return Optional.empty();
        }

        if (coordinate.getLine() == 1 && coordinate.getColumn() == 1) {
            return Optional.of(PrimitiveSymbol.UPPER_LEFT_BOX_BORDER);
        }

        if (coordinate.getLine() == 1 && coordinate.getColumn() == size.getColumns()) {
            return Optional.of(PrimitiveSymbol.UPPER_RIGHT_BOX_BORDER);
        }

        if (coordinate.getLine() == size.getLines() && coordinate.getColumn()  == 1) {
            return Optional.of(PrimitiveSymbol.LOWER_LEFT_BOX_BORDER);
        }

        if (coordinate.getLine() == size.getLines() && coordinate.getColumn() == size.getColumns()) {
            return Optional.of(PrimitiveSymbol.LOWER_RIGHT_BOX_BORDER);
        }

        if (coordinate.getLine() == 1 || coordinate.getLine() == size.getLines()) {
            return Optional.of(PrimitiveSymbol.HORIZONTAL_BOX_BORDER);
        }

        if (coordinate.getColumn() == 1 || coordinate.getColumn() == size.getColumns()) {
            return Optional.of(PrimitiveSymbol.VERTICAL_BOX_BORDER);
        }

        return Optional.empty();
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        if (!size.isInside(coordinate)) {
            throw new OutOfDrawableException(size, coordinate);
        }

        return addBorder(coordinate, size).map(primitiveSymbol -> primitiveSymbol.highlight(Color.FOCUS, onFocus))
            .orElseGet(() -> toAddBorderBox.getSymbolAt(coordinate.changeOrigin(new Coordinate(2, 2))));

    }

    @Override
    public boolean handleInput(String key) {
        return toAddBorderBox.handleInput(key);
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        if (toAddBorderBox.focus(desiredCoordinate.changeOrigin(
            new Coordinate(Math.min(2, desiredCoordinate.getLine()), Math.min(2, desiredCoordinate.getColumn()))
        ))) {
            onFocus = true;

            return true;
        }

        return false;
    }

    @Override
    public void unfocus() {
        onFocus = false;

        toAddBorderBox.unfocus();
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        return toAddBorderBox.getFocusedCoordinate().map(coordinate -> new Coordinate(
            coordinate.getLine() + 1,
            coordinate.getColumn() + 1
        ));
    }
}
