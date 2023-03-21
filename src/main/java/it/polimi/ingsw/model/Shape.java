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

    public Shape verticalFlip() {
        return new Shape(
            new ArrayList<>(offsets.stream().map(offset -> Offset.getInstance(offset.getRowOffset(),
                    width - 1 - offset.getColumnOffset())).collect(Collectors.toList()))
        );
    }

    public static final ArrayList<Shape> DOMINOES = new ArrayList<>(Arrays.asList(
        /* #
         * #
         */
        new Shape(new ArrayList<>(Arrays.asList(
            Offset.getInstance(0, 0),
            Offset.getInstance(1, 0)
        ))),
        /* ##
         */
        new Shape(new ArrayList<>(Arrays.asList(
            Offset.getInstance(0, 0),   Offset.getInstance(0, 1)
        )))
    ));

    // https://en.wikipedia.org/wiki/Tetromino
    public static final ArrayList<Shape> TETROMINOES = new ArrayList<>(Arrays.asList(
        /*  #
         *  #
         * ##
         */
        new Shape(new ArrayList<>(Arrays.asList(
                                                            Offset.getInstance(0, 1),
                                                            Offset.getInstance(1, 1),
            Offset.getInstance(2, 0),   Offset.getInstance(2, 1)
        ))),
        /* ###
         *   #
         */
        new Shape(new ArrayList<>(Arrays.asList(
            Offset.getInstance(0, 0),   Offset.getInstance(0, 1),   Offset.getInstance(0, 2),
                                                                                                            Offset.getInstance(1, 2)
        ))),
        /* ##
         * #
         * #
         */
        new Shape(new ArrayList<>(Arrays.asList(
            Offset.getInstance(0, 0),   Offset.getInstance(0, 1),
            Offset.getInstance(1, 0),
            Offset.getInstance(2, 0)
        ))),
        /* #
         * ###
         */
        new Shape(new ArrayList<>(Arrays.asList(
            Offset.getInstance(0, 0),
            Offset.getInstance(1, 0),   Offset.getInstance(1, 1),   Offset.getInstance(1, 2)
        ))),
        /*  ##
         * ##
         */
        new Shape(new ArrayList<>(Arrays.asList(
                                                            Offset.getInstance(0, 1), Offset.getInstance(0, 2),
            Offset.getInstance(1, 0), Offset.getInstance(1, 1)
        ))),
        /* #
         * ##
         *  #
         */
        new Shape(new ArrayList<>(Arrays.asList(
            Offset.getInstance(0, 0),
            Offset.getInstance(1, 0),   Offset.getInstance(1, 1),
                                                            Offset.getInstance(2, 1)
        ))),
        /* ###
         *  #
         */
        new Shape(new ArrayList<>(Arrays.asList(
            Offset.getInstance(0, 0),   Offset.getInstance(0, 1),   Offset.getInstance(0, 2),
                                                            Offset.getInstance(1, 1)
        ))),
        /* #
         * ##
         * #
         */
        new Shape(new ArrayList<>(Arrays.asList(
            Offset.getInstance(0, 0),
            Offset.getInstance(1, 0),   Offset.getInstance(1, 1),
            Offset.getInstance(2, 0)
        ))),
        /* ####
         */
        new Shape(new ArrayList<>(Arrays.asList(
            Offset.getInstance(0, 0), Offset.getInstance(0, 1), Offset.getInstance(0, 2), Offset.getInstance(0, 3)
        ))),
        /* ##
         * ##
         */
        new Shape(new ArrayList<>(Arrays.asList(
            Offset.getInstance(0, 0),   Offset.getInstance(0, 1),
            Offset.getInstance(1, 0),   Offset.getInstance(1, 1)
        ))),
        /* ##
         *  #
         *  #
         */
        new Shape(new ArrayList<>(Arrays.asList(
            Offset.getInstance(0, 0),   Offset.getInstance(0, 1),
                                                            Offset.getInstance(1, 1),
                                                            Offset.getInstance(2, 1)
        ))),
        /*   #
         * ###
         */
        new Shape(new ArrayList<>(Arrays.asList(
                                                                                                        Offset.getInstance(0, 2),
            Offset.getInstance(1, 0),   Offset.getInstance(1, 1), Offset.getInstance(1, 2)
        ))),
        /* #
         * #
         * ##
         */
        new Shape(new ArrayList<>(Arrays.asList(
            Offset.getInstance(0, 0),
            Offset.getInstance(1, 0),
            Offset.getInstance(2, 0),   Offset.getInstance(2, 1)
        ))),
        /* ###
         * #
         */
        new Shape(new ArrayList<>(Arrays.asList(
            Offset.getInstance(0, 0),   Offset.getInstance(0, 1),   Offset.getInstance(0, 2),
            Offset.getInstance(1, 0)
        ))),
        /* ##
         *  ##
         */
        new Shape(new ArrayList<>(Arrays.asList(
            Offset.getInstance(0, 0),   Offset.getInstance(0, 1),
                                                            Offset.getInstance(1, 1),   Offset.getInstance(1, 2)
        ))),
        /*  #
         * ##
         * #
         */
        new Shape(new ArrayList<>(Arrays.asList(
                                                            Offset.getInstance(0, 1),
            Offset.getInstance(1, 0),   Offset.getInstance(1, 1),
            Offset.getInstance(2, 0)
        ))),
        /*  #
         * ###
         */
        new Shape(new ArrayList<>(Arrays.asList(
                                                            Offset.getInstance(0, 1),
            Offset.getInstance(1, 0),   Offset.getInstance(1, 1),   Offset.getInstance(1, 2)
        ))),
        /*  #
         * ##
         *  #
         */
        new Shape(new ArrayList<>(Arrays.asList(
                                                            Offset.getInstance(0, 1),
            Offset.getInstance(1, 0),   Offset.getInstance(1, 1),
                                                            Offset.getInstance(2, 1)
        ))),
        /* #
         * #
         * #
         * #
         */
        new Shape(new ArrayList<>(Arrays.asList(
            Offset.getInstance(0, 0),
            Offset.getInstance(1, 0),
            Offset.getInstance(2, 0),
            Offset.getInstance(3, 0)
        )))
    ));

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
