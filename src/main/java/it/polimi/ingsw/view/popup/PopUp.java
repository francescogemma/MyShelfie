package it.polimi.ingsw.view.popup;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class PopUp {
    private final Object queueLock;

    private final String kind;
    private final String text;
    private final Object data;
    private boolean toDisplay = true;

    private final Consumer<PopUp> onshow;
    private final Consumer<PopUp> onhide;

    public PopUp(Object queueLock, String kind, String text, Object data,
                 Consumer<PopUp> onshow, Consumer<PopUp> onhide) {
        this.queueLock = queueLock;
        this.kind = kind;
        this.text = text;
        this.data = data;

        this.onshow = onshow;
        this.onhide = onhide;
    }

    public Optional<String> getKind() {
        return Optional.ofNullable(kind);
    }

    public String getText() {
        return text;
    }

    public Optional<Object> getData() {
        return Optional.ofNullable(data);
    }

    public boolean isToDisplay() {
        return toDisplay;
    }

    public void askToHide() {
        synchronized (queueLock) {
            toDisplay = false;

            queueLock.notifyAll();
        }
    }

    void show() {
        onshow.accept(this);
    }

    void hide() {
        onhide.accept(this);
    }

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