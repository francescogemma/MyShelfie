package it.polimi.ingsw.view.tui.terminal.drawable.crop;

import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.align.AlignedDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

/**
 * Adds to the contained {@link Drawable} a scrollbar, which allows to reduce its size on screen by seeing only a
 * portion of it. The displayed portion is centered around the area on focus of the contained drawable.
 *
 * @author Cristiano Migali
 */
public class ScrollableDrawable extends Drawable {
    /**
     * Is the contained {@link Drawable} to which a scrollbar will be added.
     */
    private final Drawable toMakeScrollable;

    /**
     * Is true iff the number of lines of the contained Drawable is greater than the desired (available) number,
     * hence we need vertical scrolling to reduce its height.
     */
    private boolean needsVerticalScrolling;

    /**
     * Is true iff the number of columns of the contained Drawable is greater than the desired (available) number,
     * hence we need horizontal scrolling to reduce its width.
     */
    private boolean needsHorizontalScrolling;

    /**
     * Is the number of lines of the visible portion of the contained drawable.
     */
    private int visibleLines;

    /**
     * Is the number of columns of the visible portion of the contained drawable.
     */
    private int visibleColumns;

    /**
     * Is the line component of the {@link Coordinate} of the last focused area inside the contained Drawable, or 1 if
     * it has never been on focus.
     */
    private int focusedLine;

    /**
     * Is the column component of the {@link Coordinate} of the last focused area inside the contained Drawable,
     * or 1 if it has never been on focus.
     */
    private int focusedColumn;

    /**
     * Is the line component of the upper left {@link Coordinate} of the area of the contained Drawable which will be
     * displayed on screen, eventually surrounded by scroll bars.
     */
    private int firstVisibleLine;

    /**
     * Is the column component of the upper left {@link Coordinate} of the area of the contained Drawable which will be
     * displayed on screen, eventually surrounded by scroll bars.
     */
    private int firstVisibleColumn;

    /**
     * Is the last focused coordinate of the contained drawable, or (1, 1) if the drawable has never been on focus.
     */
    private Coordinate lastFocusedCoordinate = Coordinate.origin();

    /**
     * Constructor of the class.
     * It initializes the Drawable to which the scroll bar will be added.
     *
     * @param toMakeScrollable is the Drawable to which the scroll bar will be added.
     */
    public ScrollableDrawable(AlignedDrawable toMakeScrollable) {
        this.toMakeScrollable = toMakeScrollable;
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        toMakeScrollable.askForSize(desiredSize);

        needsVerticalScrolling = toMakeScrollable.getSize().getLines() > desiredSize.getLines();
        needsHorizontalScrolling = toMakeScrollable.getSize().getColumns() > desiredSize.getColumns();

        if (needsVerticalScrolling && !needsHorizontalScrolling) {
            toMakeScrollable.askForSize(new DrawableSize(
                desiredSize.getLines(),
                desiredSize.getColumns() - 3
            ));

            needsHorizontalScrolling = toMakeScrollable.getSize().getColumns() + 3 > desiredSize.getColumns();
        } else if (!needsVerticalScrolling && needsHorizontalScrolling) {
            toMakeScrollable.askForSize(new DrawableSize(
                desiredSize.getLines() - 3,
                desiredSize.getColumns()
            ));

            needsVerticalScrolling = toMakeScrollable.getSize().getLines() + 3 > desiredSize.getLines();
        }

        if (!needsVerticalScrolling && !needsHorizontalScrolling) {
            size = desiredSize;
            visibleLines = size.getLines();
            visibleColumns = size.getColumns();
        } else if (needsVerticalScrolling && !needsHorizontalScrolling) {
            size = new DrawableSize(Math.max(desiredSize.getLines(), 2), Math.max(desiredSize.getColumns(), 3));
            visibleLines = size.getLines();
            visibleColumns = size.getColumns() - 3;
        } else if (!needsVerticalScrolling && needsHorizontalScrolling) {
            size = new DrawableSize(Math.max(desiredSize.getLines(), 3), Math.max(desiredSize.getColumns(), 2));
            visibleLines = size.getLines() - 3;
            visibleColumns = size.getColumns();
        } else {
            size = new DrawableSize(Math.max(desiredSize.getLines(), 5), Math.max(desiredSize.getColumns(), 3));
            visibleLines = size.getLines() - 3;
            visibleColumns = size.getColumns() - 3;
        }

        focusedLine = lastFocusedCoordinate.getLine();
        focusedColumn = lastFocusedCoordinate.getColumn();

        firstVisibleLine = 1;
        firstVisibleColumn = 1;

        toMakeScrollable.getFocusedCoordinate().ifPresent(coordinate -> {
            focusedLine = coordinate.getLine();
            focusedColumn = coordinate.getColumn();

            lastFocusedCoordinate = coordinate;
        });

        if (needsVerticalScrolling) {
            firstVisibleLine = focusedLine - (visibleLines / 2);

            if (firstVisibleLine < 1) {
                firstVisibleLine = 1;
            }

            if (firstVisibleLine > toMakeScrollable.getSize().getLines() - visibleLines + 1) {
                firstVisibleLine = toMakeScrollable.getSize().getLines() - visibleLines + 1;
            }
        }

        if (needsHorizontalScrolling) {
            firstVisibleColumn = focusedColumn - (visibleColumns / 2);

            if (firstVisibleColumn < 1) {
                firstVisibleColumn  = 1;
            }

            if (firstVisibleColumn > toMakeScrollable.getSize().getColumns() - visibleColumns + 1) {
                firstVisibleColumn = toMakeScrollable.getSize().getColumns() - visibleColumns + 1;
            }
        }
    }

    /**
     * Returns the symbol of the drawable at the specified coordinate accounting for the space eventually occupied by
     * the horizontal scroll bar.
     *
     * @param coordinate is the {@link Coordinate} where we want to retrieve the {@link Symbol} from.
     * @param startLine is the line component of the upper left {@link Coordinate} of the area in the ScrollableDrawable
     *                  which is below the eventual horizontal scroll bar. That is 1 if there is no horizontal bar or
     *                  4 (the horizontal bar has an height of 3) otherwise.
     * @return the {@link Symbol} at the specified {@link Coordinate}, accounting for the space occupied by the
     * horizontal scroll bar.
     */
    private Symbol getSymbolAtAfterDrawingHorizontalBar(Coordinate coordinate, int startLine) {
        if (needsVerticalScrolling) {
            return WithBorderBoxDrawable.addBorder(new Coordinate(
                coordinate.getLine() - startLine + 1,
                coordinate.getColumn()), new DrawableSize(size.getLines() - startLine + 1, 3))
                .map(Symbol.class::cast)
                .orElseGet(() -> {
                    int verticalBarLength = visibleLines * (size.getLines() - 2 - startLine + 1) /
                        toMakeScrollable.getSize().getLines();

                    int verticalBarStart = focusedLine * (size.getLines() - 2 - startLine + 1 - verticalBarLength + 1)
                        / toMakeScrollable.getSize().getLines();

                    if (verticalBarStart < 1) {
                        verticalBarStart = 1;
                    }

                    if (coordinate.getColumn() == 2) {
                        if (coordinate.getLine() - startLine >= verticalBarStart && coordinate.getLine() - startLine <
                            verticalBarStart + verticalBarLength) {
                            return PrimitiveSymbol.VERTICAL_BOX_BORDER;
                        } else {
                            return PrimitiveSymbol.EMPTY;
                        }
                    }

                    return toMakeScrollable.getSymbolAt(new Coordinate(
                       coordinate.getLine() - startLine + firstVisibleLine,
                        coordinate.getColumn() - 4 + firstVisibleColumn
                    ));
                });
        }

        return toMakeScrollable.getSymbolAt(new Coordinate(
            coordinate.getLine() - startLine + firstVisibleLine,
            coordinate.getColumn() - 1 + firstVisibleColumn
        ));
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        if (!size.isInside(coordinate)) {
            throw new OutOfDrawableException(size, coordinate);
        }

        if (needsHorizontalScrolling) {
            return WithBorderBoxDrawable.addBorder(coordinate, new DrawableSize(3, size.getColumns()))
                .map(Symbol.class::cast)
                .orElseGet(() -> {
                    int horizontalBarLength = visibleColumns * (size.getColumns() - 2) /
                        toMakeScrollable.getSize().getColumns();

                    int horizontalBarStart = focusedColumn * (size.getColumns() - 2 - horizontalBarLength + 1)
                        / toMakeScrollable.getSize().getColumns();

                    if (horizontalBarStart < 1) {
                        horizontalBarStart  = 1;
                    }

                    if (coordinate.getLine() == 2) {
                        if (coordinate.getColumn() - 1 >= horizontalBarStart && coordinate.getColumn() - 1
                            < horizontalBarStart + horizontalBarLength) {
                            return PrimitiveSymbol.HORIZONTAL_BOX_BORDER;
                        } else {
                            return PrimitiveSymbol.EMPTY;
                        }
                    }

                    return getSymbolAtAfterDrawingHorizontalBar(coordinate, 4);
                });
        }

        return getSymbolAtAfterDrawingHorizontalBar(coordinate, 1);
    }

    @Override
    public boolean handleInput(String key) {
        return toMakeScrollable.handleInput(key);
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        return toMakeScrollable.focus(new Coordinate(
            desiredCoordinate.getLine() + firstVisibleLine - 1,
            desiredCoordinate.getColumn() + firstVisibleColumn - 1
        ));
    }

    @Override
    public void unfocus() {
        toMakeScrollable.unfocus();
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        return toMakeScrollable.getFocusedCoordinate().map(
            coordinate -> new Coordinate(
                Math.max(coordinate.getLine() - firstVisibleLine + 1, 1),
                Math.max(coordinate.getColumn() - firstVisibleColumn + 1, 1)
            )
        );
    }
}
