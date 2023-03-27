package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a free shape inside a {@link Bookshelf bookshelf}, that is a set of {@link Shelf shelves} with
 * a certain topology. With the term "free" we mean that we care only about the relative locations between
 * the shelves that compose the shape, ignoring the absolute position of them inside the bookshelf.
 * For example a set of shelves that forms a 2x2 square in the top-left corner of the bookshelf has the same
 * shape of another set which forms a 2x2 square in the down-right corner. Note that the shelves do not need to form
 * a connected group.
 * Shape objects are immutable.
 *
 * <p>
 * Let's introduce some definitions that we are going to use further on:
 * <ul>
 *     <li>
 *         <b>Shape vs shape instance</b>: when we use the term shape we refer to the topology of a certain set of shelves
 *         (for example a 2x2 square), instead, when we use the term shape instance we refer to a set of shelves with
 *         a certain topology in a certain location of the bookshelf (for example a 2x2 square in the top-left corner
 *         of the bookshelf).
 *     </li>
 *     <li>
 *         <b>Bounding box</b>: imagine an instance of a certain shape inside the bookshelf. The bounding box is the
 *         <u>shape</u> of the smallest set of shelves forming a rectangle which contains all the shelves in the
 *         instance. It is clear that all the instances of the same shape have the same bounding box (remember
 *         that the bounding box is a shape and not a shape instance).
 *     </li>
 * </ul>
 *
 * @author Cristiano Migali
 */
public class Shape {
    /**
     * The width of the shape bounding box.
     *
     * @see Shape Bounding box
     */
    private final int width;

    /**
     * The height of the shape bounding box.
     *
     * @see Shape Bounding box
     */
    private final int height;

    /**
     * Consider the instance of a shape with shelves {s_i} contained inside the instance of its bounding box.
     * Let o be the shelf in the top-left corner of the bounding box instance.
     * Then we define offset_i as the {@link Offset offset} that describes the movement from o to s_i.
     * Now we can define offsets as the sequence offset_1, ..., offset_n (where n is the number of shelves in the shape
     * instance) such that the row component of offset_i is less than the one of offset_(i+1) or, if they have the same
     * row component, the column component of offset_i is less than the one of offset_(i+1).
     * In this way (having introduced an order), two shape instances are instances of the same shape
     * iff they have the same offsets.
     *
     * @see Shape Bounding box
     * @see Offset
     */
    private final ArrayList<Offset> offsets;

    /**
     * Constructor of the class.
     * It initializes {@link Shape#offsets} attribute and calculates width and height of the bounding box.
     * It creates a shape with the topology specified by offsets.
     *
     * @param offsets is a sequence of {@link Offset offsets} which define the topology of the shape in the way
     *                described below. Imagine an instance of the shape you want to create. Surround it with the instance
     *                of its bounding box. Hence, for each shelf inside the instance of your shape,
     *                we can define an offset which describes the movement from the top-left shelf of the instance of the
     *                bounding box to that shelf. offsets is the sequence of all these offsets ordered in a certain way:
     *                the first offset is the one relative to the shelf in the first row (from the top) of your instance
     *                which has no other shelf to its left on that same row. The next offset refers to the first shelf
     *                to the right of the previous one, on the same row. We keep going right until
     *                there are no shelves in that row to the right of the last one anymore.
     *                Then we go down to the next row with some shelves and start again from the left-most.
     *                We continue this process until we have covered all the shelves.
     * @throws NullPointerException if offsets is null.
     * @throws IllegalArgumentException <ul>
     *     <li> if offsets is empty.</li>
     *     <li> if there is an offset which is in a row greater than the one of the following
     *          offset.</li>
     *     <li> if there is an offset which is in the same row of the following offset but
     *          in a greater column.</li>
     *     <li> if there is an offset which is equal to the following one.</li>
     *     <li> if offsets are not defined with respect to the top-left shelf of the instance
     *          of the bounding box.</li>
     * </ul>
     *
     * @see Offset
     * @see Shape Bounding box
     */
    public Shape(ArrayList<Offset> offsets) {
        if (offsets == null) {
            throw new NullPointerException("offsets must be non-null when constructing a shape");
        }

        if (offsets.isEmpty()) {
            throw new IllegalArgumentException("offsets must be non-empty when constructing a shape");
        }

        for (int i = 0; i < offsets.size() - 1; i++) {
            if (offsets.get(i).getRowOffset() > offsets.get(i + 1).getRowOffset()) {
                throw new IllegalArgumentException("Found that offset at index " + i + " has row component " +
                    offsets.get(i).getRowOffset() + " which is greater than the one of the following offset: " +
                    offsets.get(i + 1).getRowOffset());
            }

            if (offsets.get(i).getRowOffset() == offsets.get(i + 1).getRowOffset()) {
                if (offsets.get(i).getColumnOffset() > offsets.get(i + 1).getColumnOffset()) {
                    throw new IllegalArgumentException("Found that offset at index " + i + " is on the same row " +
                        "of the following offset but has column component " + offsets.get(i).getColumnOffset() +
                        " which is greater than the one of the following offset: " +
                        offsets.get(i + 1).getColumnOffset());
                }

                if (offsets.get(i).getColumnOffset() == offsets.get(i + 1).getColumnOffset()) {
                    throw new IllegalArgumentException("Found that offset at index " + i + " is equal to the following "
                        + "offset");
                }
            }
        }

        int minRow = offsets.get(0).getRowOffset();
        int minColumn = offsets.get(0).getColumnOffset();
        int maxRow = minRow;
        int maxColumn = minColumn;
        Shelf currentShelf;

        // Reconstructing a shape instance with the top-left cell of its bounding box instance in the origin.
        for (Offset offset : offsets) {
            try {
                currentShelf = Shelf.origin().move(offset);
            } catch (RuntimeException e) {
                throw new IllegalArgumentException("offsets are not defined with respect to the top-left " +
                    "shelf of the instance of the bounding box");
            }

            minRow = Math.min(minRow, currentShelf.getRow());
            minColumn = Math.min(minColumn, currentShelf.getColumn());
            maxRow = Math.max(maxRow, currentShelf.getRow());
            maxColumn = Math.max(maxColumn, currentShelf.getColumn());
        }

        /*
         * If the offsets are defined with respect to the top-left shelf of the instance of the bounding box,
         * then, if we reconstruct a shape instance putting the top-left shelf of its bounding box instance
         * in the origin, there must be at least one shelf in column 0 and at least one shelf in row 0.
         */
        if (minRow != 0 || minColumn != 0) {
            throw new IllegalArgumentException("offsets are not defined with respect to the top-left " +
                "shelf of the instance of the bounding box");
        }

        height = maxRow + 1;
        width = maxColumn + 1;

        this.offsets = new ArrayList<>(offsets);
    }

    /**
     * @return the width of the bounding box of the shape.
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height of the bounding box of the shape.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the sequence of offsets which defines the topology of the shape.
     */
    public ArrayList<Offset> getOffsets() {
        return new ArrayList<>(offsets);
    }

    /**
     * @return a new shape which is obtained by flipping the original one along the vertical axis.
     * The original shape is not affected since it's immutable.
     */
    public Shape verticalFlip() {
        ArrayList<Offset> flippedOffsets = new ArrayList<>();

        /*
         * In order to vertically flip the shape while keeping the offsets list
         * invariant property (the one about the order) satisfied, we must invert the order
         * of offsets relative to the same row when we flip and add them to the final list.
         * For doing so, we can keep track of the index in the final list of the first offset
         * in the row that we are flipping and add new offsets relative to that same row
         * before that index.
         */
        int currentRowInsertionIndex = 0;
        for (int i = 0; i < offsets.size(); i++) {
            if (i == 0 || (offsets.get(i).getRowOffset() > offsets.get(i - 1).getRowOffset())) {
                currentRowInsertionIndex = flippedOffsets.size();
            }

            flippedOffsets.add(currentRowInsertionIndex,
                Offset.getInstance(offsets.get(i).getRowOffset(),
                    width - 1 - offsets.get(i).getColumnOffset()));
        }

        return new Shape(flippedOffsets);
    }

    /**
     * A list with all the dominoes, that is shapes with 2 connected shelves.
     */
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

    /**
     * The shape with all 4 corners of a {@link Bookshelf bookshelf}.
     */
    public static final Shape CORNERS = new Shape(new ArrayList<>(Arrays.asList(
        Offset.getInstance(0, 0), Offset.getInstance(0, 4),
        Offset.getInstance(5, 0), Offset.getInstance(5, 4)
    )));

    /**
     * A list with all the tetrominoes, that is shapes with 4 connected shelves.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Tetromino">Wikipedia page of tetrimonoes</a>
     */
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

    public static final Shape SQUARE = new Shape(new ArrayList<>(Arrays.asList(
        Offset.getInstance(0, 0), Offset.getInstance(0, 1),
        Offset.getInstance(1, 0), Offset.getInstance(1, 1)
    )));

    /**
     * @param height is the height of the column.
     * @return the shape of a column with the specified height.
     * @throws IllegalArgumentException if height is not positive.
     * @throws IllegalArgumentException if the column doesn't fit inside a {@link Bookshelf bookshelf}.
     */
    public static Shape getColumn(int height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Height of a column must be positive");
        }

        ArrayList<Offset> columnOffsets = new ArrayList<>();

        for (int i = 0; i < height; i++) {
            try {
                columnOffsets.add(Offset.down(i));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("The column doesn't fit inside the bookshelf");
            }
        }

        return new Shape(columnOffsets);
    }

    /**
     * @param size is the number of shelves in the diagonal.
     * @return the shape of a main (directed down-right) diagonal with the specified size.
     * @throws IllegalArgumentException if size is not positive.
     * @throws IllegalArgumentException if the diagonal doesn't fit inside a {@link Bookshelf bookshelf}.
     */
    public static Shape getMainDiagonal(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size of a diagonal must be positive");
        }

        ArrayList<Offset> diagonalOffsets = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            try {
                diagonalOffsets.add(Offset.getInstance(i, i));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("The diagonal doesn't fit inside the bookshelf");
            }
        }

        return new Shape(diagonalOffsets);
    }

    /**
     * @param width is the width of the row.
     * @return the shape of a row with the specified width.
     * @throws IllegalArgumentException if width is not positive
     * @throws IllegalArgumentException if the row doesn't fit inside a {@link Bookshelf bookshelf}.
     */
    public static Shape getRow(int width) {
        if (width <= 0) {
            throw new IllegalArgumentException("Width of a row must be positive");
        }

        ArrayList<Offset> rowOffsets = new ArrayList<>();

        for (int i = 0; i < width; i++) {
            try {
                rowOffsets.add(Offset.right(i));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("The row doesn't fit inside the bookshelf");
            }
        }

        return new Shape(rowOffsets);
    }

    /**
     * The shape of a 3x3 X.
     */
    public static final Shape X = new Shape(new ArrayList<>(Arrays.asList(
        Offset.getInstance(0, 0), Offset.getInstance(0, 2),
        Offset.getInstance(1, 1),
        Offset.getInstance(2, 0), Offset.getInstance(2, 2)
    )));

    /**
     * The width and height of an {@link Shape#ENLARGED_STAIR enlarged stair}.
     */
    private static final int STAIR_SIDE = 5;

    /**
     * The shape of a stair directed down-right with height and width equal to {@value STAIR_SIDE}.
     * It is said to be enlarged since above each column forming the stair after the first one from the left
     * (which is the higher), there is an additional shelf.
     */
    public static final Shape ENLARGED_STAIR;

    // Builds the ENLARGED_STAIR
    static {
        ArrayList<Offset> offsets = new ArrayList<>();

        for (int row = 0; row < STAIR_SIDE; row++) {
            for (int column = 0; column < STAIR_SIDE; column++) {
                /*
                 * With this condition the first two column of the stair are full (height 5),
                 * then the stair starts to go down from the third column (column 2).
                 */
                if (row >= column - 1) {
                    offsets.add(Offset.getInstance(row, column));
                }
            }
        }

        ENLARGED_STAIR = new Shape(offsets);
    }

    /**
     * The shape of the set of all shelves inside a {@link Bookshelf bookshelf}.
     */
    public static final Shape WHOLE_BOOKSHELF;

    // Builds the WHOLE_BOOKSHELF
    static {
        ArrayList<Offset> offsets = new ArrayList<>();

        for (int row = 0; row < STAIR_SIDE; row++) {
            for (int column = 0; column < STAIR_SIDE; column++) {
                offsets.add(Offset.getInstance(row, column));
            }
        }

        WHOLE_BOOKSHELF = new Shape(offsets);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (!(other instanceof Shape)) {
            return false;
        }

        Shape otherShape = (Shape) other;

        return offsets.equals(otherShape.offsets);
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
