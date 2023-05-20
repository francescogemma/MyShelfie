package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.align.CenteredDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.align.DownRightAlignedDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.align.UpLeftAlignedDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayoutElement;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

/**
 * Represents every object which can be print on a rectangular portion of the terminal.
 *
 * A drawable is characterized by a size, that is the number of lines and columns it occupies on the terminal.
 * We can ask a drawable to fit a certain size, but there is no guarantee that the drawable will resize to the
 * desired size. However it is a good practice to always ask for the desired size to a drawable, in order to make
 * it fit the available space hopefully in the best manner.
 *
 * A drawable can provide a {@link it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol} for every
 * {@link Coordinate} in { 1, ..., lines } x { 1, ..., columns } which allows to print it on screen.
 *
 * Drawable are also responsible for handling user input (through the keyboard). In fact a drawable can be "on focus"
 * or not, if it is on focus it will receive the user input provided by {@link it.polimi.ingsw.view.tui.terminal.Terminal}.
 *
 * @see DrawableSize
 *
 * @author Cristiano Migali
 */
public abstract class Drawable {
    /**
     * Current size of the drawable, that is the number of lines and columns it occupies in the terminal.
     */
    protected DrawableSize size;

    /**
     * @return the current size of the drawable.
     */
    public final DrawableSize getSize() {
        return size;
    }

    /**
     * @return the coordinate of its center in its reference frame.
     */
    public final Coordinate getCenter() {
        return new Coordinate(size.getLines() / 2 + 1, size.getColumns() / 2 + 1);
    }

    /**
     * Asks the drawable to assume a desired size. There is no guarantee on the current size of the drawable after
     * the invocation of this method.
     *
     * @param desiredSize is the desired size we would like the drawable to assume.
     */
    public abstract void askForSize(DrawableSize desiredSize);

    /**
     * @param coordinate is the coordinate where we want to retrieve the {@link it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol}
     *                   which allows to print the drawbale on screen.
     * @return the {@link it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol} at the specified
     * {@link Coordinate} which allows to print the drawable on screen.
     * @throws OutOfDrawableException if the {@link Coordinate} is outside the rectangular portion of the screen
     * defined by the drawable size.
     */
    public abstract Symbol getSymbolAt(Coordinate coordinate);

    /**
     * Asks the drawable to handle the provided user input (in the form of a keyboard key press).
     *
     * @param key is the string associated with the keyboard key pressed by the user.
     *
     * @return true iff the drawable has actually handled the input.
     */
    public abstract boolean handleInput(String key);

    /**
     * Asks the drawable to become on focus. It specifies which area of the drawable should be in focus through
     * the given {@link Coordinate}, this is relevant for drawables which are composed of several children drawables,
     * each of which can be on focus or not.
     *
     * @param desiredCoordinate specifies the coordinate of the area in the drawable we want to become on focus.
     *
     * @return true iff the drawable supports focus, that is it can actually be on focus and handle user input.
     *
     * @see it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout
     */
    public abstract boolean focus(Coordinate desiredCoordinate);

    /**
     * Makes the drawable not in focus if it was on focus, otherwise it does nothing.
     */
    public abstract void unfocus();

    /**
     * @return an optional which is empty if the drawable is not in focus, otherwise contains the {@link Coordinate}
     * of the area in the drawable which is in focus. This is relevant for drawables which are composed of several
     * children drawables, each of which can be on focus or not.
     *
     * @see it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout
     */
    public abstract Optional<Coordinate> getFocusedCoordinate();

    /**
     * @param weight is weight of the drawable in the {@link it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout}.
     * @return an {@link OrientedLayoutElement} which contains this drawable, with the given weight.
     */
    public OrientedLayoutElement weight(int weight) {
        return new OrientedLayoutElement(this, weight);
    }

    /**
     * @return a {@link CenteredDrawable} which contains this drawable.
     */
    public CenteredDrawable center() {
        return new CenteredDrawable(this);
    }

    /**
     * @return an {@link UpLeftAlignedDrawable} which contains this drawable.
     */
    public UpLeftAlignedDrawable alignUpLeft() {
        return new UpLeftAlignedDrawable(this);
    }

    /**
     * @return an {@link DownRightAlignedDrawable} which contains this drawable.
     */
    public DownRightAlignedDrawable alignDownRight() { return new DownRightAlignedDrawable(this); }

    /**
     * @return a {@link WithBorderBoxDrawable} which contains this drawable.
     */
    public WithBorderBoxDrawable addBorderBox() {
        return new WithBorderBoxDrawable(this);
    }

    /**
     * @return a {@link BlurrableDrawable} which contains this drawable.
     */
    public BlurrableDrawable blurrable() {
        return new BlurrableDrawable(this);
    }
}
