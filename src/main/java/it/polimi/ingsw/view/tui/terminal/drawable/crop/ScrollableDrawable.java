package it.polimi.ingsw.view.tui.terminal.drawable.crop;

import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.align.AlignedDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

public class ScrollableDrawable extends Drawable {
    private final Drawable toMakeScrollable;

    private boolean needsVerticalScrolling;
    private boolean needsHorizontalScrolling;

    private int visibleLines;
    private int visibleColumns;

    private int focusedLine;
    private int focusedColumn;

    private int firstVisibleLine;
    private int firstVisibleColumn;

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

        focusedLine = 1;
        focusedColumn = 1;

        firstVisibleLine = 1;
        firstVisibleColumn = 1;

        toMakeScrollable.getFocusedCoordinate().ifPresent(coordinate -> {
            focusedLine = coordinate.getLine();
            focusedColumn = coordinate.getColumn();
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
        return toMakeScrollable.focus(Coordinate.origin());
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
