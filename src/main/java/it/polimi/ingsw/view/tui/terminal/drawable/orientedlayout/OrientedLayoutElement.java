package it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout;

import it.polimi.ingsw.view.tui.terminal.drawable.Drawable;

/**
 * Represents a child of an {@link OrientedLayout}, that is a Drawable with a weight proportional to the space
 * that it occupies in the layout.
 *
 * @author Cristiano Migali
 */
public class OrientedLayoutElement {
    /**
     * It is the underlying Drawable to which a weight has been assigned.
     */
    private final Drawable drawable;

    /**
     * It is a non-negative integer which corresponds to the weight that the Drawable should occupy in the layout.
     */
    private int weight;

    /**
     * @param weight is the non-negative integer which corresponds to the weight of the Drawable in the layout.
     *
     * @throws IllegalArgumentException iff weight is negative.
     */
    private static void assertWeightIsValid(int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Layout element weight must be non-negative, got: " + weight);
        }
    }

    /**
     * Constructor of the class.
     * Initializes a layout element with the given Drawable and weight.
     *
     * @param drawable is the underlying Drawable to which the weight will be assigned.
     * @param weight is the weight associated with the Drawable in the layout, proportional to the space that
     *               it should occupy.
     */
    public OrientedLayoutElement(Drawable drawable, int weight) {
        assertWeightIsValid(weight);

        this.drawable = drawable;
        this.weight = weight;
    }

    /**
     * @return the weight associated with the layout child, which should be proportional to the space that it occupies.
     */
    public int getWeight() {
        return weight;
    }

    /**
     * @return the underlying Drawable to which a weight has been assigned.
     */
    public Drawable getDrawable() {
        return drawable;
    }

    /**
     * Sets the weight for this layout child.
     *
     * @param weight is the weight that will be assigned to the underlying Drawable, which should be proportional to
     *               the space that it occupies.
     */
    public void setWeight(int weight) {
        this.weight = weight;

        if (weight == 0) {
            drawable.unfocus();
        }
    }
}
