package it.polimi.ingsw.utils;

import java.util.List;

public class Pair<T, F> {
    private T key;
    private F value;
    public Pair(T key, F value) {
        this.key = key;
        this.value = value;
    }

    public T getKey() {
        return key;
    }

    public F getValue() {
        return value;
    }

    public void setKey(T row) {
        this.key = row;
    }

    public void setValue(F f) {
        this.value = f;
    }

    public static <T, F> Pair<T, F> of(T key, F value) {
        return new Pair<>(key, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj instanceof Pair) {
            Pair pair = (Pair) obj;
            if (key != null ? !key.equals(pair.key) : pair.key != null)
                return false;
            if (value != null ? !value.equals(pair.value) : pair.value != null)
                return false;
            return true;
        }

        return false;
    }
}
