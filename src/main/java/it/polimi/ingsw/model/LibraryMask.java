package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.function.BiFunction;

public class LibraryMask {
    private final Library library;
    private final ArrayList<Shelf> shelves = new ArrayList<>();

    public LibraryMask(Library library) {
        this.library = library;
    }

    public void clear() {
        shelves.clear();
    }

    public void add(Shelf shelf) {
        for (int i = 0; i < shelves.size(); i++) {
            if (shelf.equals(shelves.get(i))) {
                throw new RuntimeException(shelf + " has already been inserted in the mask");
            }

            if (shelf.before(shelves.get(i))) {
                shelves.add(i, shelf);
                break;
            }
        }

        shelves.add(shelf);
    }
    public ArrayList<Shelf> getShelves() {
        return new ArrayList<>(shelves);
    }
    // gets the first tile in the mask
    public Tile getSampleTile() {
        return library.get(shelves.get(0));
    }
    public static final BiFunction<LibraryMask, LibraryMask, Boolean> DO_NOT_INTERSECT = (first, second) -> {
        for (Shelf A : first.shelves) {
            for (Shelf B : second.shelves) {
                if (A.equals(B)) {
                    return false;
                }
            }
        }
        return true;
    };

    public static final BiFunction<LibraryMask, LibraryMask, Boolean> DO_NOT_INTERSECT_AND_SAME_COLOR = (first, second) ->
        first.getSampleTile() == second.getSampleTile() && DO_NOT_INTERSECT.apply(first, second);

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("---------------\n");

        for (int row = 0; row < Library.ROWS; row++) {
            for (int column = 0; column < Library.COLUMNS; column++) {
                Shelf currentShelf = Shelf.getInstance(row, column);
                String toColor = " ";
                if (shelves.contains(currentShelf)) {
                    toColor = "#";
                }

                result.append("[").append(library.get(currentShelf).color(toColor)).append("]");
            }

            result.append("\n---------------\n");
        }

        return result.toString();
    }
}
