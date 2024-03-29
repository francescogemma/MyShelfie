package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.tile.TileColor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * Represents a mutable set of {@link Shelf shelves} inside a {@link Bookshelf bookshelf}.
 *
 * @author Cristiano Migali
 */
public class BookshelfMask {
    /**
     * The bookshelf this mask is referred to.
     */
    private final Bookshelf bookshelf;

    /**
     * The {@link Shelf shelves} in the set.
     */
    private final List<Shelf> shelves;

    /**
     * Constructor of the class.
     *
     * @param bookshelf is the bookshelf the created mask is referred to.
     */
    public BookshelfMask(Bookshelf bookshelf) {
        this.bookshelf = bookshelf;
        shelves = new ArrayList<>();
    }

    /**
     * Copy constructor for the class.
     *
     * @param bookshelfMask is the bookshelf mask to copy.
     */
    public BookshelfMask(BookshelfMask bookshelfMask) {
        bookshelf = bookshelfMask.bookshelf;
        shelves = new ArrayList<>(bookshelfMask.shelves);
    }

    /**
     * Removes all the {@link Shelf shelves} from the set.
     */
    public void clear() {
        shelves.clear();
    }

    /**
     * Adds a {@link Shelf shelf} to the set.
     *
     * @param shelf is the shelf that we are going to add to the set.
     * @throws DuplicateShelfException if the shelf is already in the set.
     */
    public void add(Shelf shelf) {
        for (int i = 0; i < shelves.size(); i++) {
            if (shelf.equals(shelves.get(i))) {
                throw new DuplicateShelfException(shelf);
            }

            if (shelf.before(shelves.get(i))) {
                shelves.add(i, shelf);
                return;
            }
        }

        shelves.add(shelf);
    }

    /**
     * @return all the {@link Shelf shelves} inside the set.
     */
    public List<Shelf> getShelves() {
        return new ArrayList<>(shelves);
    }

    /**
     * @return the {@link TileColor color} of the tile inside a shelf in the set.
     *
     * @author Michele Miotti
     */
    public TileColor getSampleTileColor() {
        return bookshelf.getTileColorAt(shelves.get(0));
    }

    /**
     * Lambda function which returns true iff the two bookshelf masks in input do not intersect, that is the intersection
     * of their sets is empty.
     */
    public static final BiPredicate<BookshelfMask, BookshelfMask> DO_NOT_INTERSECT = (first, second) -> {
        for (Shelf A : first.shelves) {
            for (Shelf B : second.shelves) {
                if (A.equals(B)) {
                    return false;
                }
            }
        }
        return true;
    };

    /**
     * Lambda function which returns true iff the two bookshelf masks in input do not intersect, that is the intersection
     * of their sets is empty, and all the tiles contained in shelves inside a mask or the other are of the same color.
     * A precondition of this function is that all the tiles contained in shelves inside the same mask must be of the
     * same color.
     */
    public static final BiPredicate<BookshelfMask, BookshelfMask> DO_NOT_INTERSECT_AND_SAME_COLOR = (first, second) ->
        first.getSampleTileColor() == second.getSampleTileColor() && DO_NOT_INTERSECT.test(first, second);

    /**
     * Gets the color of the tile inside the specified shelf.
     * @param shelf is where we're sampling for color
     * @return the {@link TileColor tile color} at that location.
     *
     * @author Michele Miotti
     */
    public TileColor getTileColorAt(Shelf shelf) {
        return bookshelf.getTileColorAt(shelf);
    }

    /**
     * Count the number of shelves of a specific color in the mask.
     * @param tileColor is the color we're counting
     * @return the number of shelves of that color in the mask.
     * @throws IllegalArgumentException if we are trying to count empty shelves.
     *
     * @author Francesco Gemma
     */
    public int countTilesOfColor(TileColor tileColor) {
        int count = 0;

        if(tileColor == TileColor.EMPTY) {
            throw new IllegalArgumentException("Cannot count empty tiles");
        }

        for(Shelf shelf : shelves) {
            if(getTileColorAt(shelf) == tileColor) {
                count++;
            }
        }

        return count;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (!(other instanceof BookshelfMask)) {
            return false;
        }

        BookshelfMask otherMask = (BookshelfMask) other;

        return shelves.equals(otherMask.shelves);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("---------------\n");

        for (int row = 0; row < Bookshelf.ROWS; row++) {
            for (int column = 0; column < Bookshelf.COLUMNS; column++) {
                Shelf currentShelf = Shelf.getInstance(row, column);
                String toColor = " ";
                if (shelves.contains(currentShelf)) {
                    toColor = "#";
                }

                result.append("[").append(bookshelf.getTileColorAt(currentShelf).color(toColor)).append("]");
            }

            result.append("\n---------------\n");
        }

        return result.toString();
    }
}
