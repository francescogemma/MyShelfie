package it.polimi.ingsw.model;

import java.util.ArrayList;

public class ShapeFetcher implements Fetcher {
    private final ArrayList<Offset> offsets;
    int nextOffsetIndex = 0;

    private Shelf shapeOrigin = Shelf.origin();

    private final Shelf lastOrigin;

    public ShapeFetcher(Shape shape) {
        this.offsets = shape.getOffsets();

        lastOrigin = Shelf.getInstance(Library.ROWS - shape.getHeight(),
            Library.COLUMNS - shape.getWidth());
    }

    @Override
    public Shelf next() {
        if (nextOffsetIndex == offsets.size()) {
            throw new RuntimeException();
        }

        return shapeOrigin.move(offsets.get(nextOffsetIndex++));
    }

    private void nextShape() {
        nextOffsetIndex = 0;

        if (shapeOrigin == lastOrigin) {
                shapeOrigin = Shelf.origin();
        } else {
            if (shapeOrigin.getColumn() < lastOrigin.getColumn()) {
                shapeOrigin = shapeOrigin.move(Offset.right());
            } else {
                shapeOrigin = Shelf.origin().move(Offset.down(shapeOrigin.getRow() + 1));
            }
        }
    }

    @Override
    public boolean lastShelf() {
        if (nextOffsetIndex == offsets.size()) {
            nextShape();

            return true;
        }

        return false;
    }

    @Override
    public boolean hasFinished() {
        return shapeOrigin == Shelf.origin() && nextOffsetIndex == 0;
    }

    @Override
    public boolean canFix() {
        nextShape();

        return false;
    }
}
