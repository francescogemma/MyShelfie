package it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout;

import it.polimi.ingsw.view.tui.terminal.drawable.Drawable;

public class OrientedLayoutElement {
    private final Drawable drawable;
    private int weight;

    private static void assertWeightIsValid(int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Layout element weight must be non-negative, got: " + weight);
        }
    }

    public OrientedLayoutElement(Drawable drawable, int weight) {
        assertWeightIsValid(weight);

        this.drawable = drawable;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
