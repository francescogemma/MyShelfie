package it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout;

import it.polimi.ingsw.view.tui.terminal.drawable.Drawable;

public class OrientedLayoutElement {
    private final Drawable drawable;
    private int weight;

    private static void assertWeightIsValid(int weight) {
        if (weight < 1 || weight > 100) {
            throw new IllegalArgumentException("Layout element weight must be between 1 and 100, got: " + weight);
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

    public void setWeight(int weight) {
        assertWeightIsValid(weight);

        this.weight = weight;
    }

    public Drawable getDrawable() {
        return drawable;
    }
}
