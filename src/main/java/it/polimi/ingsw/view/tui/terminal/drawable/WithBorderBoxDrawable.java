package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

public class WithBorderBoxDrawable extends Drawable {
    private final Drawable toAddBorderBox;

    private boolean onFocus = false;

    private static Color color = Color.RED;

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

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        if (!size.isInside(coordinate)) {
            throw new OutOfDrawableException(size, coordinate);
        }

        if (coordinate.getLine() == 1 && coordinate.getColumn() == 1) {
            return PrimitiveSymbol.UPPER_LEFT_BOX_BORDER.highlight(color, onFocus);
        }

        if (coordinate.getLine() == 1 && coordinate.getColumn() == size.getColumns()) {
            return PrimitiveSymbol.UPPER_RIGHT_BOX_BORDER.highlight(color, onFocus);
        }

        if (coordinate.getLine() == size.getLines() && coordinate.getColumn()  == 1) {
            return PrimitiveSymbol.LOWER_LEFT_BOX_BORDER.highlight(color, onFocus);
        }

        if (coordinate.getLine() == size.getLines() && coordinate.getColumn() == size.getColumns()) {
            return PrimitiveSymbol.LOWER_RIGHT_BOX_BORDER.highlight(color, onFocus);
        }

        if (coordinate.getLine() == 1 || coordinate.getLine() == size.getLines()) {
            return PrimitiveSymbol.HORIZONTAL_BOX_BORDER.highlight(color, onFocus);
        }

        if (coordinate.getColumn() == 1 || coordinate.getColumn() == size.getColumns()) {
            return PrimitiveSymbol.VERTICAL_BOX_BORDER.highlight(color, onFocus);
        }

        return toAddBorderBox.getSymbolAt(coordinate.changeOrigin(new Coordinate(2, 2)));
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

    public static void color(Color color) {
        WithBorderBoxDrawable.color = color;
    }
}
