package it.polimi.ingsw.view.popup;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
 * Represents a pop-up which can be displayed on a user interface.
 * A pop-up is characterized by a text message which will be displayed on screen and a kind which allows
 * to group together pop-ups that inform the user of different happenings of the same event.
 * Furthermore it is possible to add some data (in the form of an Object) to the pop-up.
 *
 * @see PopUpQueue
 *
 * @author Cristiano Migali
 */
public class PopUp {
    /**
     * It is the lock on which the pop-up queue which created this pop-up synchronizes.
     * The pop-up synchronizes on this lock when it performs the hide request to the queue.
     * In fact the pop-up has no reference to its queue.
     * This is the only way of hiding a pop-up.
     * In this way the component which have access to the {@link PopUpQueue} can't directly hide the pop-ups.
     *
     * @see PopUpQueue
     */
    private final Object queueLock;

    /**
     * It is the kind of the pop-up which allows to group it with other pop-ups that inform the user of different
     * happenings of the event associated with this pop-up.
     */
    private final String kind;

    /**
     * It is the text message of the pop-up.
     */
    private final String text;

    /**
     * It is the data Object eventually attached to the pop-up.
     */
    private final Object data;

    /**
     * It is true iff the pop-up has still to be displayed on the interface.
     */
    private boolean toShow = true;

    /**
     * It is true iff the pop-up should be hidden from the interface.
     */
    private boolean toHide = false;

    /**
     * Callback invoked when the pop-up is shown on the interface.
     */
    private final Consumer<PopUp> onshow;

    /**
     * Callback invoked when the pop-up is hidden from the interface.
     */
    private final Consumer<PopUp> onhide;

    /**
     * Constructor of the class.
     * It initializes all the required attributes.
     *
     * @param queueLock it is the lock object on which the {@link PopUpQueue} that is creating this pop-up
     *                  synchronizes.
     * @param kind is a string which allows to group together pop-ups that inform the user of different happenings of
     *             the same event.
     * @param text is the text message of the pop-up.
     * @param data is the data Object which can be associated to the pop-up.
     * @param onshow is the callback invoked when the pop-up is displayed on the interface.
     * @param onhide is the callback invoked when the pop-up is hidden from the interface.
     */
    public PopUp(Object queueLock, String kind, String text, Object data,
                 Consumer<PopUp> onshow, Consumer<PopUp> onhide) {
        this.queueLock = queueLock;
        this.kind = kind;
        this.text = text;
        this.data = data;

        this.onshow = onshow;
        this.onhide = onhide;
    }

    /**
     * @return the text message of this pop-up.
     */
    public String getText() {
        return text;
    }

    /**
     * @return an Optional which contains the data Object associated with this pop-up if it has one, an empty Optional
     * otherwise.
     */
    public Optional<Object> getData() {
        return Optional.ofNullable(data);
    }

    /**
     * Asks the {@link PopUpQueue} to hide this pop-up from the interface. This is the onmly way of doing so.
     */
    public void askToHide() {
        synchronized (queueLock) {
            toHide = true;

            queueLock.notifyAll();
        }
    }

    /**
     * @return an Optional with the kind of this pop-up if it has one, an empty Optional otherwise.
     */
    Optional<String> getKind() {
        return Optional.ofNullable(kind);
    }

    /**
     * @return true iff this pop-up has to be shown on the interface.
     */
    boolean isToShow() {
        return toShow;
    }

    /**
     * @return true iff this pop-up has to be hidden from the interface.
     */
    boolean isToHide() {
        return toHide;
    }

    /**
     * Allows to set that this pop-up has been shown on the interface.
     */
    void setShown() {
        toShow = false;
    }

    /**
     * Allows to set that this pop-up has been hidden from the interface.
     */
    void setHidden() {
        toHide = false;
    }

    /**
     * Invokes the callback which gets notified when the pop-up has been shown on the interface.
     */
    void onshow() {
        onshow.accept(this);
    }

    /**
     * Invokes the callback which gets notified when the pop-up has been hidden from the interface.
     */
    void onhide() {
        onhide.accept(this);
    }

    /**
     * Default callback that should be invoked when the pop-up has been shown to hide it after a fixed time interval.
     *
     * @param milliseconds is the number of milliseconds of the time interval after which we want to hide the pop-up
     *                     from the interface.
     * @return the callback which, if invoked when the pop-up has been shown, hides the pop-up after the specified
     * fixed time interval.
     */
    public static Consumer<PopUp> hideAfter(long milliseconds) {
        return popUp -> {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    popUp.askToHide();
                }
            }, milliseconds);
        };
    }
}
