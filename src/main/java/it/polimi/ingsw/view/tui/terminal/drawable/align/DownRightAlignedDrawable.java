package it.polimi.ingsw.view.tui.terminal.drawable.align;

import it.polimi.ingsw.view.tui.terminal.drawable.Coordinate;
import it.polimi.ingsw.view.tui.terminal.drawable.Drawable;
import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;
import it.polimi.ingsw.view.tui.terminal.drawable.OutOfDrawableException;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

/**
 * Is an AlignedDrawable which aligns a given inner {@link Drawable} to the right-down side of the free space
 * (if there is some).
 *
 * @see AlignedDrawable
 */
public class DownRightAlignedDrawable extends AlignedDrawable {
    /**
     * The inner Drawable which must be aligned to the right-down side of the free space.
     */
    private final Drawable toAlignDownRight;

    /**
     * The line component of the {@link Coordinate} of the origin (upper-left corner) of the inner Drawable inside
     * the free space.
     */
    private int toAlignDownRightOriginLine;

    /**
     * The column component of the {@link Coordinate} of the origin (upper-left corner) of the inner Drawable inside
     * the free space.
     */
    private int toAlignDownRightOriginColumn;

    /**
     * Constructor of the class. Initializes the inner Drawable that is going to be aligned to the right-down side
     * of the available space.
     *
     * @param toAlignDownRight is the inner Drawable that will be aligned to the right-down side of the available space
     *                         (if there is some).
     */
    public DownRightAlignedDrawable(Drawable toAlignDownRight) {
        this.toAlignDownRight = toAlignDownRight;
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        toAlignDownRight.askForSize(desiredSize);

        int actualLines = Math.max(desiredSize.getLines(), toAlignDownRight.getSize().getLines());
        int actualColumns = Math.max(desiredSize.getColumns(), toAlignDownRight.getSize().getColumns());

        toAlignDownRightOriginLine = 1 + actualLines - toAlignDownRight.getSize().getLines();
        toAlignDownRightOriginColumn = 1 + actualColumns - toAlignDownRight.getSize().getColumns();

        size = new DrawableSize(actualLines, actualColumns);
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        if (!size.isInside(coordinate)) {
            throw new OutOfDrawableException(size, coordinate);
        }

        if (coordinate.getLine() < toAlignDownRightOriginLine || coordinate.getColumn() < toAlignDownRightOriginColumn) {
            return PrimitiveSymbol.EMPTY;
        }

        return toAlignDownRight.getSymbolAt(coordinate.changeOrigin(new Coordinate(toAlignDownRightOriginLine,
            toAlignDownRightOriginColumn)));
    }

    @Override
    public boolean handleInput(String key) {
        return toAlignDownRight.handleInput(key);
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        int adjustedDesiredLine = 1;
        int adjustedDesiredColumn = 1;

        if (desiredCoordinate.getLine() > toAlignDownRightOriginLine) {
            adjustedDesiredLine += desiredCoordinate.getLine() - toAlignDownRightOriginLine;
        }

        if (desiredCoordinate.getColumn() > toAlignDownRightOriginColumn) {
            adjustedDesiredColumn += desiredCoordinate.getColumn() - toAlignDownRightOriginColumn;
        }

        return toAlignDownRight.focus(new Coordinate(adjustedDesiredLine, adjustedDesiredColumn));
    }

    @Override
    public void unfocus() {
        toAlignDownRight.unfocus();
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        return toAlignDownRight.getFocusedCoordinate();
    }
}
