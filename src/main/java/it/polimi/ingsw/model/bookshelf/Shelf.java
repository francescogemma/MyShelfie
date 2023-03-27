package it.polimi.ingsw.model.bookshelf;

/**
 * Represents the location of a single cell inside a {@link Bookshelf bookshelf};
 * that is a couple of integers (row, column) where row is an integer between 0 and the number of rows
 * in a bookshelf ({@value Bookshelf#ROWS}) minus 1 and column is an integer between 0 and the number
 * of columns in a bookshelf ({@value Bookshelf#COLUMNS}) minus 1.
 * Rows are enumerated starting from 0 which corresponds to the first row from the top of the bookshelf.
 * Columns are enumerated starting from 0 too, which corresponds to the first column to the left in the bookshelf.
 * Shelf objects are immutable.
 *
 * @see Bookshelf
 *
 * @author Cristiano Migali
 */
public class Shelf {
    /**
     * The bookshelf row in which the shelf is located.
     */
    private final int row;

    /**
     * The bookshelf column in which the shelf is located.
     */
    private final int column;

    /**
     * Matrix where all Shelf instances are stored. It is used to implement a singleton pattern,
     * indeed the number of different Shelf instances is finite and corresponds to the number of cells in the
     * bookshelf: {@value Bookshelf#ROWS}*{@value Bookshelf#COLUMNS}. A shelf with row = i and column = j is stored
     * in instances[i][j].
     */
    private static final Shelf[][] instances = new Shelf[Bookshelf.ROWS][Bookshelf.COLUMNS];

    /**
     * Constructor of the class.
     * It initializes {@link Shelf#row} and {@link Shelf#column} attributes.
     * The constructor is private since we are implementing a singleton pattern, hence we don't need to check that the
     * arguments belong to the right domain; the check is already done in {@link Shelf#getInstance(int, int)}.
     *
     * @see Shelf#instances
     * @see Shelf#getInstance(int, int)
     *
     * @param row is the bookshelf row where the shelf is located.
     * @param column is the bookshelf column where the shelf is located.
     */
    private Shelf(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /*
     * It is necessary to create all instances of Shelf before the main is executed, because if two
     * threads were to request the same instance of Shelf, and it does not exist, creating it could
     * generate concurrency problems, and thus issues in the code.
     * @author Giacomo Groppi
     * */
    static {
        for (int i = 0; i < instances.length; i++) {
            for (int j = 0; j < instances[i].length; j++) {
                instances[i][j] = new Shelf(i, j);
            }
        }
    }

    /**
     * Retrieves a shelf in the specified location. This is the only way of doing so: this class implements
     * a singleton pattern. For this reason Shelf objects in the same location (row, column) of the bookshelf are actually
     * the same object in memory.
     *
     * @param row is the bookshelf row where the returned shelf is located. It must be between 0 and
     *            {@value Bookshelf#ROWS} - 1.
     * @param column is the bookshelf column where the returned shelf is located. It must be between 0 and
     *               {@value Bookshelf#COLUMNS} - 1.
     * @return a shelf in location (row, column).
     * @throws IllegalArgumentException if arguments are outside the domain {0, ..., {@value Bookshelf#ROWS} - 1}
     * x {0, ..., {@value Bookshelf#COLUMNS} - 1}.
     */
    public static Shelf getInstance(int row, int column) {
        if (!Bookshelf.isRowInsideTheBookshelf(row) || !Bookshelf.isColumnInsideTheBookshelf(column)) {
            throw new IllegalArgumentException("shelf at (" + row + ", " + column + ") is not inside the bookshelf");
        }

        return instances[row][column];
    }


    /**
     * @return the shelf in location (0, 0), that is the top-left cell.
     */
    public static Shelf origin() {
        return getInstance(0, 0);
    }

    /**
     * @param offset is the translation vector of the movement. It specifies the number of rows we are going to shift
     *               the shelf down and the number of columns we are going to shift the shelf right
     *               (negative values of offset components allow moving up and left respectively).
     * @return a shelf in location (row + offset.getRowOffset(), column + offset.getColumnOffset()) where (row, column)
     * is the location of the shelf on which this method is invoked. The original shelf is not affected since it's
     * immutable.
     * @throws RuntimeException if we try to move a shelf to a location which is outside the bookshelf.
     */
    public Shelf move(Offset offset) {
        if (!Bookshelf.isRowInsideTheBookshelf(row + offset.getRowOffset()) ||
            !Bookshelf.isColumnInsideTheBookshelf(column + offset.getColumnOffset())) {
            throw new RuntimeException("Trying to move " + this + " of " + offset);
        }

        return getInstance(row + offset.getRowOffset(), column + offset.getColumnOffset());
    }


    /**
     * @return the bookshelf row in which the shelf is located.
     */
    public int getRow() {
        return row;
    }

    /**
     * @return the bookshelf column in which the shelf is located.
     */
    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }


    /**
     * @param other shelf with which we are comparing the shelf on which this method is invoked.
     * @return true iff ((row is less than otherRow) or (row is equal to otherRow and column is less than otherColumn))
     * where the shelf on which this method is invoked is in (row, column), while the other shelf is in
     * (otherRow, otherColumn).
     */
    public boolean before(Shelf other) {
        if (other == null) {
            throw new NullPointerException();
        }

        if (row != other.row) {
            return row < other.row;
        }

        return column < other.column;
    }

    @Override
    public String toString() {
        return "shelf at (" + row + ", " + column + ")";
    }
}
