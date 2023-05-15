package it.polimi.ingsw.utils;

import java.util.List;
import java.util.Objects;

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
        if (this == obj) return true;

        if (obj == null || obj.getClass() != this.getClass())
            return false;

        if (!Objects.equals(key, ((Pair<?, ?>) obj).key))
            return false;
        return Objects.equals(value, ((Pair<?, ?>) obj).value);
    }
}
