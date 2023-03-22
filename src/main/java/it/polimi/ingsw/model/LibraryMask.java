package it.polimi.ingsw.model;

import java.util.ArrayList;

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
    public boolean intersects(LibraryMask libraryMask) {
        for (Shelf A : shelves) {
            for (Shelf B : libraryMask.getShelves()) {
                if (A.getRow() == B.getRow() && A.getColumn() == B.getColumn()) {
                    return true;
                }
            }
        }
        return false;
    }
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
