package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

/**
 * It is a Drawable obtained by adding a border box to another Drawable.
 * The border box is built through {@link PrimitiveSymbol#VERTICAL_BOX_BORDER}, {@link PrimitiveSymbol#HORIZONTAL_BOX_BORDER},
 * {@link PrimitiveSymbol#LOWER_RIGHT_BOX_BORDER}, {@link PrimitiveSymbol#LOWER_LEFT_BOX_BORDER},
 * {@link PrimitiveSymbol#UPPER_RIGHT_BOX_BORDER} and {@link PrimitiveSymbol#UPPER_LEFT_BOX_BORDER} symbols.
 * When the surrounded Drawable is on focus, the border box turns red.
 *
 * @author Cristiano Migali
 */
public class WithBorderBoxDrawable extends Drawable {
    /**
     * It is the Drawable to which the border box will be added.
     */
    private final Drawable toAddBorderBox;

    /**
     * It is true iff the WithBorderBoxDrawable is on focus.
     */
    private boolean onFocus;

    /**
     * Constructor of the class.
     *
     * @param toAddBorderBox is the Drawable to which the border box will be added.
     */
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

        onFocus = toAddBorderBox.getFocusedCoordinate().isPresent();
    }

    /**
     * Allows to add border boxes with the specified {@link DrawableSize} to a Drawable.
     *
     * @param coordinate is the {@link Coordinate} where we want to retrieve the corresponding border box symbol.
     * @param size is the {@link DrawableSize} of the border box that has to be added to the Drawable (it is not
     *             the size of the Drawable, it comprehends also the border).
     * @return an Optional which is empty if the {@link Coordinate} isn't on the border of the rectangle identified
     * by the given {@link DrawableSize}, otherwise it contains the {@link PrimitiveSymbol}
     * of the border box at the specified {@link Coordinate}.
     */
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
        return toAddBorderBox.focus(desiredCoordinate.changeOrigin(
            new Coordinate(Math.min(2, desiredCoordinate.getLine()), Math.min(2, desiredCoordinate.getColumn()))
        ));
    }

    @Override
    public void unfocus() {
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
