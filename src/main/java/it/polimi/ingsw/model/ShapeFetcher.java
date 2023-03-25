package it.polimi.ingsw.model;

import java.util.ArrayList;

/**
 * Allows to extract all the {@link LibraryMask library masks} inside a {@link Library library} with a
 * certain {@link Shape shape}.
 *
 * @author Cristiano Migali
 */
public class ShapeFetcher implements Fetcher {
    /**
     * The offsets of the {@link Shape shape} that the fetcher extracts.
     *
     * @see Shape
     */
    private final ArrayList<Offset> offsets;

    /**
     * The index of the offset of the {@link Shape shape} that allows to calculate the next {@link Shelf shelf}
     * that will be extracted. In particular, such a shelf is equal to
     * {@code shapeOrigin.move(offsets.get(nextOffsetIndex))}.
     */
    int nextOffsetIndex = 0;

    /**
     * The top-left {@link Shelf shelf} of the bounding box's instance of the library mask that we are
     * extracting. (Note that a library mask can be regarded as the instance of a shape)
     *
     * @see Shape Shape's bounding box and shape instance definitions
     */
    private Shelf shapeOrigin = Shelf.origin();

    /**
     * The last {@link ShapeFetcher#shapeOrigin} from which we can extract a library mask with the given shape.
     * With "last" we refer to the ordering introduced by {@link Shelf#before(Shelf)}. If we go further
     * there isn't enough space for a library mask with the given shape to fit inside the library (with "further"
     * we refer to the ordering introduced by {@link Shelf#before(Shelf)} too).
     */
    private final Shelf lastOrigin;

    public ShapeFetcher(Shape shape) {
        this.offsets = shape.getOffsets();

        /*
         * A shape instance fits inside a library iff its bounding box's instance fits inside the library
         * (since the library is rectangular). Hence, to determine the last origin, we should find
         * the last shelf from which a bounding box's instance of the shape would fit inside the library.
         * Since the bounding box is a rectangle, such a shelf is quite easy to calculate.
         */
        lastOrigin = Shelf.getInstance(Library.ROWS - shape.getHeight(),
            Library.COLUMNS - shape.getWidth());
    }

    @Override
    public Shelf next() {
        /*
         * lastShelf is in charge of bringing nextOffsetIndex back to 0 if nextOffsetIndex == offsets.size().
         * Since we must always call lastShelf after a call to next, it can never happen that
         * nextOffsetIndex == offsets.size().
         */
        if (nextOffsetIndex == offsets.size()) {
            throw new RuntimeException();
        }

        return shapeOrigin.move(offsets.get(nextOffsetIndex++));
    }

    /**
     * Sets up the fetcher for the extraction of the next library mask.
     * After we extracted the last library mask, it brings the fetcher back to its equilibrium state.
     */
    private void nextShape() {
        nextOffsetIndex = 0;

        if (shapeOrigin == lastOrigin) {
                // We cycle back to the first, after having extracted the last library mask.
                shapeOrigin = Shelf.origin();
        } else {
            if (shapeOrigin.getColumn() < lastOrigin.getColumn()) {
                // We move the shape origin right while the library mask bounding box still fits inside the library.
                shapeOrigin = shapeOrigin.move(Offset.right());
            } else {
                // Otherwise we move the shape origin at the the beginning of the next row.
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
        /*
         * The fetcher is in its equilibrium shape iff the shape origin corresponds with the origin of the library,
         * and we still have to extract the first shelf of the shape instance (nextOffsetIndex == 0).
         */
        return shapeOrigin == Shelf.origin() && nextOffsetIndex == 0;
    }

    @Override
    public boolean canFix() {
        // We must set up for the extraction of the next shape.
        nextShape();

        /*
         * There is no way to fix a shape. The shelves that constitute a shape instance starting from a certain
         * origin can't change.
         */
        return false;
    }
}
