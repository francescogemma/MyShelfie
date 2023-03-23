package it.polimi.ingsw.model;

public class Pair<T, F> {
    private T key;
    private F value;
    Pair(T row, F col) {
        this.key = row;
        this.value = col;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj instanceof Pair pair) {
            if (pair.key != null ^ this.key != null) {
                return false;
            }

            if (pair.key != null && !this.key.equals(pair.key))
                return false;

            if (pair.value != null ^ this.value != null) {
                return false;
            }

            if (pair.value != null && !this.value.equals(pair.value))
                return false;
        }

        return false;
    }
}
