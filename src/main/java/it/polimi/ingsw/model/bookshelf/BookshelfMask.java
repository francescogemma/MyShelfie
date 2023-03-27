package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.Tile;

import java.util.ArrayList;
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
    private final ArrayList<Shelf> shelves = new ArrayList<>();

    /**
     * Constructor of the class.
     *
     * @param bookshelf is the bookshelf the created mask is referred to.
     */
    public BookshelfMask(Bookshelf bookshelf) {
        this.bookshelf = bookshelf;
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
     * @throws RuntimeException if the shelf is already in the set.
     */
    public void add(Shelf shelf) {
        for (int i = 0; i < shelves.size(); i++) {
            if (shelf.equals(shelves.get(i))) {
                throw new RuntimeException(shelf + " has already been inserted in the mask");
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
    public ArrayList<Shelf> getShelves() {
        return new ArrayList<>(shelves);
    }

    /**
     * @return the tile inside a shelf in the set.
     *
     * @author Michele Miotti
     */
    public Tile getSampleTile() {
        return bookshelf.get(shelves.get(0));
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
        first.getSampleTile() == second.getSampleTile() && DO_NOT_INTERSECT.test(first, second);

    /**
     * Gets a specific shelf's content.
     * @param shelf is where we're sampling for color
     * @return the {@link Tile tile} at that location.
     *
     * @author Michele Miotti
     */
    public Tile tileAt(Shelf shelf) {
        return bookshelf.get(shelf);
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

                result.append("[").append(bookshelf.get(currentShelf).color(toColor)).append("]");
            }

            result.append("\n---------------\n");
        }

        return result.toString();
    }
}
