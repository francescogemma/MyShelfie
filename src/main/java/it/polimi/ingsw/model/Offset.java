package it.polimi.ingsw.model;

/**
 * Represents a translation vector that describes the movement from a {@link Shelf shelf} to another, inside
 * a {@link Library library}. That is a couple of integers (rowOffset, columnOffset) such that
 * (rowSecond, columnSecond) = (rowFirst, columnFirst) + (rowOffset, columnOffset) where (rowFirst, columnFirst)
 * is the location of the first shelf, (rowSecond, columnSecond) is the location of the second shelf
 * and the translation goes from the first shelf to the second.
 * Offset objects are immutable.
 *
 * @see Shelf
 * @see Shelf#move(Offset)
 *
 * @author Cristiano Migali
 */
public class Offset {
    /**
     * The amount of rows by which we are going to shift a shelf in the translation. Positive values
     * represent downward translations, negative values represent upward translations.
     */
    private final int rowOffset;

    /**
     * The amount of columns by which we are going to shift a shelf in the translation. Positive values
     * represent translations to the right, negative values represent translations to the left.
     */
    private final int columnOffset;

    /**
     * Matrix where all Offset instances are stored. It is used to implement a singleton pattern,
     * indeed the number of different Offset instances is finite. The minimum value for {@link Offset#rowOffset}
     * is -({@value Library#ROWS} - 1) (when we translate a shelf from the last row to the first),
     * its maximum value is {@value Library#ROWS} - 1 (when we translate a shelf from the first row to the last),
     * for a total of 2*{@value Library#ROWS} - 1 values.
     * Analogously the minimum value for {@link Offset#columnOffset} is -({@value Library#COLUMNS} - 1) and its
     * maximum value is {@value Library#COLUMNS} - 1, for a total of 2*{@value Library#COLUMNS} - 1.
     * Hence the total number of different Offset instances is
     * (2*{@value Library#ROWS} - 1)*(2*{@value Library#COLUMNS} - 1). An offset with rowOffset = i and columnOffset = j
     * is stored in instances[i + ({@value Library#ROWS} - 1)][j + ({@value Library#COLUMNS} - 1)] (such that when
     * the value of a component of the vector is minimum, the corresponding index where it is stored is 0).
     *
     * @see Shelf Rows and columns enumeration convetions
     */
    private static final Offset[][] instances = new Offset[2*Library.ROWS - 1][2*Library.COLUMNS - 1];

    /**
     * Constructor of the class.
     * It initializes {@link Offset#rowOffset} and {@link  Offset#columnOffset} attributes.
     * The constructor is private since we are implementing a singleton pattern, hence we don't need to check that the
     * arguments belong to the right domain: the check is already done in {@link Offset#getInstance(int, int)}.
     *
     * @see Offset#instances
     * @see Offset#getInstance(int, int)
     *
     * @param rowOffset is the amount of rows by which the shelf is going to be shifted in the translation.
     * @param columnOffset is the amount of columns by which the shelf is going to be shifted in the translation.
     */
    private Offset(int rowOffset, int columnOffset) {
        this.rowOffset = rowOffset;
        this.columnOffset = columnOffset;
    }

    /*
     * It is necessary to create all instances of Offset before the main is executed, because if two
     * threads were to request the same instance of Offset, and it does not exist, creating it could
     * generate concurrency problems, and thus issues in the code.
     * @author Giacomo Groppi
     * */
    static {
        for(int i = 0; i < Offset.instances.length; i++) {
            for (int j = 0; j < Offset.instances[i].length; j++) {
                instances[i][j] = new Offset(
                        i - Library.ROWS + 1,
                        j - Library.COLUMNS + 1
                );
            }
        }
    }

    /**
     * Retrieves an offset with the specified components. This the only way of doing so: this class implements a singleton
     * pattern. For this reason Offset objects with the same components (rowOffset, columnOffset) are actually the same
     * object in memory.
     *
     * @param rowOffset is the amount of rows by which the shelf is going to be shifted in the translation. It must be
     *                  between -({@value Library#ROWS} - 1) and {@value Library#ROWS} - 1. Negative values result in
     *                  upward translations, positive values result in downward translations.
     * @param columnOffset is the amount of columns by which the shelf is going to be shifted in the translation. It
     *                     must be between -({@value Library#COLUMNS} - 1) and {@value Library#COLUMNS} - 1. Negative
     *                     values result in translations to the left, positive values result in translations to the right.
     * @return an offset with components (rowOffset, columnOffset).
     * @throws IllegalArgumentException if arguments are outside the domain {-({@value Library#ROWS} - 1), ...,
     * {@value Library#ROWS} - 1} x {-({@value Library#COLUMNS} - 1), ..., {@value Library#COLUMNS} - 1}.
     */
    public static Offset getInstance(int rowOffset, int columnOffset) {
        if (rowOffset <= - Library.ROWS || rowOffset >= Library.ROWS) {
            throw new IllegalArgumentException("row offset must be between " + (-Library.ROWS + 1) + " and "
            + (Library.ROWS - 1));
        }

        if (columnOffset <= -Library.COLUMNS || columnOffset >= Library.COLUMNS) {
            throw new IllegalArgumentException("column offset must be between " + (-Library.COLUMNS + 1) + " and "
            + (Library.COLUMNS - 1));
        }

        if (instances[Library.ROWS - 1 + rowOffset][Library.COLUMNS - 1 + columnOffset] == null) {
            instances[Library.ROWS - 1 + rowOffset][Library.COLUMNS - 1 + columnOffset] =
                new Offset(rowOffset, columnOffset);
        }

        return instances[Library.ROWS - 1 + rowOffset][Library.COLUMNS - 1 + columnOffset];
    }

    /**
     * @return an offset representing an upward translation of magnitude 1.
     */
    public static Offset up() {
        return up(1);
    }

    /**
     * @param amount is the magnitude of the upward translation. It can also be negative, in that case the
     *               resulting translation is downward.
     * @return an offset with components (-amount, 0) representing a vertical translation.
     */
    public static Offset up(int amount) {
        return getInstance(-amount, 0);
    }

    /**
     * @return an offset representing a downward translation of magnitude 1.
     */
    public static Offset down() {
        return down(1);
    }

    /**
     * @param amount is the magnitude of the downward translation. It can also be negative, in that case the
     *               resulting translation is upward.
     * @return an offset with components (amount, 0) representing a vertical translation.
     */
    public static Offset down(int amount) {
        return getInstance(amount, 0);
    }

    /**
     * @return an offset representing a translation to the left of magnitude 1.
     */
    public static Offset left() {
        return left(1);
    }

    /**
     * @param amount is the magnitude of the translation to the left. It can also be negative, in that case the
     *               resulting translation is to the right.
     * @return an offset with components (0, -amount) representing an horizontal translation.
     */
    public static Offset left(int amount) {
        return getInstance(0, -amount);
    }

    /**
     * @return an offset representing a translation to the right of magnitude 1.
     */
    public static Offset right() {
        return right(1);
    }

    /**
     * @param amount is the magnitude of the translation to the right. It can also be negative, in that case the
     *               resulting translation is to the left.
     * @return an offset with components (0, amount) representing an horizontal translation.
     */
    public static Offset right(int amount) {
        return getInstance(0, amount);
    }

    /**
     * @return the vertical component (rowOffset) of the offset.
     */
    public int getRowOffset() {
        return rowOffset;
    }

    /**
     * @return the horizontal component (columnOffset) of the offset.
     */
    public int getColumnOffset() {
        return columnOffset;
    }

    /**
     * @param other offset that we are adding to the offset on which this method is invoked.
     * @return the sum of the offset on which this method is invoked plus other, that is
     * an offset with components (rowOffset + otherRowOffset, columnOffset, otherColumnsOffset)
     * where (otherRowOffset, otherColumnOffset) are the components of other. The original offset
     * is not affected since it's immutable.
     * @throws IllegalArgumentException if the offset resulting from the sum would move each shelf outside the library.
     * That is if the offset components are bigger than library dimensions.
     */
    public Offset add(Offset other) {
        return getInstance(rowOffset + other.rowOffset, columnOffset + other.columnOffset);
    }

    @Override
    public String toString() {
        return rowOffset + " V, " + columnOffset + " >";
    }
}
