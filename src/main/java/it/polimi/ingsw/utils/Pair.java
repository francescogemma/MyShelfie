package it.polimi.ingsw.utils;

import java.util.Objects;

/**
 * A generic class representing a pair of values.
 * @param <K> The type of the key.
 * @param <T> The type of the value.
 *
 * @author Giacomo Groppi
 */
public class Pair<K, T> {
    /**
     * The key of the Pair.
     */
    private K key;

    /**
     * The value of the Pair.
     */
    private T value;

    /**
     * Constructs a new Pair with the given key and value.
     *
     * @param key   The key of the pair.
     * @param value The value of the pair.
     */
    public Pair(K key, T value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns the key of the pair.
     *
     * @return The key of the pair.
     */
    public K getKey() {
        return key;
    }

    /**
     * Returns the value of the pair.
     *
     * @return The value of the pair.
     */
    public T getValue() {
        return value;
    }

    /**
     * Sets the key of the pair.
     *
     * @param key The key to set.
     */
    public void setKey(K key) {
        this.key = key;
    }

    /**
     * Sets the value of the pair.
     *
     * @param value The value to set.
     */
    public void setValue(T value) {
        this.value = value;
    }

    /**
     * Creates a new Pair with the given key and value.
     *
     * @param <T>   The type of the key.
     * @param <F>   The type of the value.
     * @param key   The key of the pair.
     * @param value The value of the pair.
     * @return A new Pair with the specified key and value.
     */
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
