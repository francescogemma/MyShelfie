package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class Shape {
    private final int width;
    private final int height;
    private final ArrayList<Offset> offsets;

    public Shape(ArrayList<Offset> offsets) {
        // TODO: Check that there are no duplicates in offsets

        if (offsets == null) {
            throw new NullPointerException("offsets must be non-null when constructing a shape");
        }

        if (offsets.isEmpty()) {
            throw new IllegalArgumentException("offsets must be non-empty when constructing a shape");
        }

        int maxWidth = 1;
        int maxHeight = 1;
        Shelf currentShelf;

        for (Offset offset : offsets) {
            currentShelf = Shelf.origin().move(offset);

            maxWidth = Math.max(maxWidth, currentShelf.getColumn() + 1);
            maxHeight = Math.max(maxHeight, currentShelf.getRow() + 1);
        }

        width = maxWidth;
        height = maxHeight;

        this.offsets = new ArrayList<>(offsets);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<Offset> getOffsets() {
        return new ArrayList<>(offsets);
    }

    public static ArrayList<Shape> getAllConnectedShapesOfSizeN(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("There is no shape of size 0");
        }

        ArrayList<Shape> shapes = new ArrayList<>();
        ArrayList<Offset> offsets = new ArrayList<>();

        if (n == 1) {
            offsets.add(Offset.getInstance(0, 0));
            shapes.add(new Shape(offsets));

            return shapes;
        }

        ArrayList<Shape> subShapes = getAllConnectedShapesOfSizeN(n - 1);

        boolean[][] contains = new boolean[Library.ROWS][Library.COLUMNS];

        for (Shape subShape : subShapes) {
            for (int row = 0; row < subShape.height; row++) {
                for (int column = 0; column < subShape.width; column++) {
                    contains[row][column] = false;
                }
            }

            Shelf currentShelf;
            for (Offset offset : subShape.offsets) {
                currentShelf = Shelf.origin().move(offset);
                contains[currentShelf.getRow()][currentShelf.getColumn()] = true;
            }

            // Zone 1:
            if (subShape.width < Library.COLUMNS) {
                if (contains[0][0]) {
                    offsets.clear();

                    offsets.add(Offset.getInstance(0, 0));
                    for (Offset offset : subShape.offsets) {
                        offsets.add(offset.add(Offset.right()));
                    }

                    shapes.add(new Shape(offsets));
                }
            }

            // Zone 2:
            if (subShape.height < Library.ROWS) {
                for (int column = 0; column < subShape.width; column++) {
                    if (contains[0][column]) {
                        offsets.clear();

                        offsets.add(Offset.right(column));
                        for (Offset offset : subShape.offsets) {
                            offsets.add(offset.add(Offset.down()));
                        }

                        shapes.add(new Shape(offsets));
                    }
                }
            }

            // Zone 3:
            for (int column = 0; column < subShape.width - 1; column++) {
                if (contains[0][column]) {
                    break;
                }

                if ((subShape.height > 1 && contains[1][column])
                        || contains[0][column + 1]) {
                    offsets.clear();

                    offsets.add(Offset.right(column));
                    for (Offset offset : subShape.offsets) {
                        offsets.add(offset);
                    }

                    shapes.add(new Shape(offsets));
                }
            }
        }

        return shapes;
    }

    @Override
    public String toString() {
        StringBuilder[] rows = new StringBuilder[height];
        for (int row = 0; row < height; row++) {
            rows[row] = new StringBuilder(width);

            for (int column = 0; column < width; column++) {
                rows[row].append(" ");
            }
        }

        Shelf currentShelf;
        for (Offset offset : offsets) {
            currentShelf = Shelf.origin().move(offset);
            rows[currentShelf.getRow()].setCharAt(currentShelf.getColumn(), '#');
        }

        StringBuilder result = new StringBuilder((width + 1) * height);
        for (StringBuilder row : rows) {
            result.append(row).append("\n");
        }

        return result.toString();
    }
}
