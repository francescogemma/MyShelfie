package it.polimi.ingsw.view.popup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PopUpQueue {
    private final Object lock = new Object();

    private final Consumer<String> displayPopUp;
    private final Runnable hidePopUp;

    private final List<PopUp> popUps = new ArrayList<>();

    private boolean enabled = false;

    public PopUpQueue(Consumer<String> displayPopUp, Runnable hidePopUp) {
        this.displayPopUp = displayPopUp;
        this.hidePopUp = hidePopUp;
    }

    private void add(PopUp newPopUp) {
        if (!enabled) {
            throw new IllegalStateException("You can't add pop-ups to the pop-up queue while it is disabled");
        }

        if (newPopUp.getKind().isPresent()) {
            if (popUps.stream().anyMatch(popUp -> popUp.getKind().isPresent() &&
                popUp.getKind().get().equals(newPopUp.getKind().get()))) {

                // We won't add new pop-ups with the same kind of pop-ups already in the queue.
                return;
            }
        }

        popUps.add(newPopUp);
        if (popUps.size() == 1) {
            displayPopUp.accept(newPopUp.getText());
            new Thread(newPopUp::show).start();
        }
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

    public void enable() {
        synchronized (lock) {
            if (enabled) {
                return;
            }
        }

        new Thread(() -> {
            synchronized (lock) {
                while (true) {
                    while (popUps.size() == 0 || popUps.get(0).isToDisplay()) {
                        if (!enabled) {
                            return;
                        }

                        try {
                            lock.wait();
                        } catch (InterruptedException e) { }
                    }

                    hidePopUp.run();
                    popUps.get(0).hide();
                    popUps.remove(0);

                    if (popUps.size() > 0) {
                        displayPopUp.accept(popUps.get(0).getText());
                        new Thread(popUps.get(0)::show).start();
                    }
                }
            }
        }).start();
    }

    public void disable() {
        synchronized (lock) {
            if (!enabled) {
                return;
            }

            clear();

            enabled = false;

            lock.notifyAll();
        }
    }

    public void clear() {
        synchronized (lock) {
            for (PopUp popUp : popUps) {
                popUp.hide();
            }

            popUps.clear();
        }
    }
}
