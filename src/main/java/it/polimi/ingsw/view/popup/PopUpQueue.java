package it.polimi.ingsw.view.popup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PopUpQueue {
    private final Object lock;

    private final Consumer<String> showPopUp;
    private final Runnable hidePopUp;

    private boolean enabled = true;
    private final List<PopUp> popUps = new ArrayList<>();

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

    public void add(String text, Consumer<PopUp> onshow, Consumer<PopUp> onhide) {
        synchronized (lock) {
            add(new PopUp(lock, null, text, null, onshow, onhide));
        }
    }

    public void add(String text, Object data, Consumer<PopUp> onshow, Consumer<PopUp> onhide) {
        synchronized (lock) {
            add(new PopUp(lock, null, text, data, onshow, onhide));
        }
    }

    public void add(String text, String kind, Consumer<PopUp> onshow, Consumer<PopUp> onhide) {
        synchronized (lock) {
            add(new PopUp(lock, kind, text, null, onshow, onhide));
        }
    }

    public void add(String text, String kind, Object data, Consumer<PopUp> onshow, Consumer<PopUp> onhide) {
        synchronized (lock) {
            add(new PopUp(lock, kind, text, data, onshow, onhide));
        }
    }

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
