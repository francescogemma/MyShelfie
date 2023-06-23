package it.polimi.ingsw.view.popup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a queue of {@link PopUp}s which allows to display them on a certain interface.
 * In particular the user can specify a function to display a {@link PopUp} on a certain interface and another function
 * to hide it.
 * Furthermore the user can add pop-ups to the queue.
 * The PopUpQueue is responsible for calling the function to show and hide pop-ups at the right time.
 * Each pop-up has some callbacks which can be defined by the user to control when the pop-up is hidden from the queue.
 *
 * @see PopUp
 *
 * @author Cristiano Migali
 */
public class PopUpQueue {
    /**
     * Lock object on which the queue synchronizes when adding or removing pop-ups.
     */
    private final Object lock;

    /**
     * Function which displays a pop-up on a certain interface.
     */
    private final Consumer<String> showPopUp;

    /**
     * Function which hides a pop-up from a certain interface.
     */
    private final Runnable hidePopUp;

    /**
     * It is true iff pop-up can be added to the queue.
     */
    private boolean enabled = true;

    /**
     * It is the list of pop-ups in the queue. The first pop-up in the list is the first that will be shown or it
     * is the one that is being shown.
     */
    private final List<PopUp> popUps = new ArrayList<>();

    /**
     * Constructor of the class.
     * it starts the thread responsible for showing and hiding pop-ups.
     *
     * @param showPopUp is the function which displays a pop-up on the desired interface.
     * @param hidePopUp is the function which hides a pop-up from the desired interface.
     * @param lock is the lock object on which this queue synchronizes while showing or hiding pop-ups, adding or removing
     *             them.
     */
    public PopUpQueue(Consumer<String> showPopUp, Runnable hidePopUp, Object lock) {
        this.showPopUp = showPopUp;
        this.hidePopUp = hidePopUp;
        this.lock = lock;

        new Thread(() -> {
            synchronized (this.lock) {
                while (true) {
                    while (popUps.isEmpty() || (!popUps.get(0).isToShow() && !popUps.get(0).isToHide())
                        || !enabled) {

                        if (!enabled) {
                            return;
                        }

                        try {
                            lock.wait();
                        } catch (InterruptedException e) { }
                    }

                    if (popUps.get(0).isToShow() && popUps.get(0).isToHide()) {
                        popUps.get(0).setShown();
                        popUps.get(0).onshow();
                        popUps.get(0).setHidden();
                        popUps.remove(0).onhide();
                    } else if (popUps.get(0).isToShow()) {
                        showPopUp.accept(popUps.get(0).getText());
                        popUps.get(0).setShown();
                        /* Performed in a new thread to allow long operations.
                         * Synchronization is achieved through ask to hide: it ensures that hide is
                         * performed always after show.
                         */
                        PopUp topPopUp = popUps.get(0);
                        new Thread(topPopUp::onshow).start();
                    } else {
                        hidePopUp.run();
                        popUps.get(0).setHidden();
                        popUps.remove(0).onhide();
                    }
                }
            }
        }).start();
    }

    /**
     * Adds a pop-up at the end of the queue.
     *
     * @param newPopUp is the pop-up that will be added at the end of the queue.
     */
    private void add(PopUp newPopUp) {
        if (!enabled) {
            throw new IllegalStateException("You can't add pop-ups to the pop-up queue while it is disabled");
        }

        if (newPopUp.getKind().isPresent() &&
            popUps.stream().anyMatch(popUp -> popUp.getKind().isPresent() &&
                popUp.getKind().get().equals(newPopUp.getKind().get()))) {

            // We won't add new pop-ups with the same kind of pop-ups already in the queue.
            return;
        }

        popUps.add(newPopUp);

        lock.notifyAll();
    }

    /**
     * Adds a pop-up with the provided text message and callbacks invoked respectively when the pop-up is shown
     * and hidden on the interface.
     *
     * @param text is the text message of the pop-up that has to be added to the queue.
     * @param onshow is the callback which is invoked when the pop-up added to the queue is shown on the interface.
     * @param onhide is the callback which is invoked when the pop-up added to the queue is hidden from the interface.
     */
    public void add(String text, Consumer<PopUp> onshow, Consumer<PopUp> onhide) {
        synchronized (lock) {
            add(new PopUp(lock, null, text, null, onshow, onhide));
        }
    }

    /**
     * Adds a pop-up with the provided text message, data object and callbacks invoked respectively when the pop-up
     * is shown and hidden on the interface.
     *
     * @param text is the text message of the pop-up that has to be added to the queue.
     * @param data is the data Object associated with the pop-up that has to be added to the queue.
     * @param onshow is the callback which is invoked when the pop-up added to the queue is shown on the interface.
     * @param onhide is the callback which is invoked when the pop-up added to the queue is hidden from the interface.
     */
    public void add(String text, Object data, Consumer<PopUp> onshow, Consumer<PopUp> onhide) {
        synchronized (lock) {
            add(new PopUp(lock, null, text, data, onshow, onhide));
        }
    }

    /**
     * Adds a pop-up with the provided text message, kind and callbacks invoked respectively when the pop-up
     * is shown and hidden on the interface.
     *
     * @param text is the text message of the pop-up that has to be added to the queue.
     * @param kind is the kind of the pop-up that has to be added to the queue. It allows to group together pop-ups
     *             which inform the user of different happenings of the same event.
     * @param onshow is the callback which is invoked when the pop-up added to the queue is shown on the interface.
     * @param onhide is the callback which is invoked when the pop-up added to the queue is hidden from the interface.
     *
     * @see PopUp Pop-up kind
     */
    public void add(String text, String kind, Consumer<PopUp> onshow, Consumer<PopUp> onhide) {
        synchronized (lock) {
            add(new PopUp(lock, kind, text, null, onshow, onhide));
        }
    }

    /**
     * Adds a pop-up with the provided text message, kind, data object and callbacks invoked respectively when
     * the pop-up is shown and hidden on the interface.
     *
     * @param text is the text message of the pop-up that has to be added to the queue.
     * @param kind is the kind of the pop-up that has to be added to the queue. It allows to group together pop-ups
     *             which inform the user of different happenings of the same event.
     * @param data is the data Object associated with the pop-up that has to be added to the queue.
     * @param onshow is the callback which is invoked when the pop-up added to the queue is shown on the interface.
     * @param onhide is the callback which is invoked when the pop-up added to the queue is hidden from the interface.
     */
    public void add(String text, String kind, Object data, Consumer<PopUp> onshow, Consumer<PopUp> onhide) {
        synchronized (lock) {
            add(new PopUp(lock, kind, text, data, onshow, onhide));
        }
    }

    /**
     * Disables the queue. After this method has been invoked, no pop-up can be added to the queue.
     * The queue should be thrown away and substituted with a new one.
     * This method stops the thread responsible for showing and hiding pop-ups.
     */
    public void disable() {
        synchronized (lock) {
            if (!enabled) {
                throw new IllegalStateException("You can't disable a pop-up queue which has been already disabled");
            }

            enabled = false;

            lock.notifyAll();
        }
    }
}
