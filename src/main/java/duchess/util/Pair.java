package duchess.util;

/**
 * Represents a pair of items,
 * possibly of different types.
 */
public class Pair<U, V> {
    private U first;
    private V second;

    /**
     * Creates a pair with the 2 specified
     * elements.
     */
    public Pair(U first, V second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first element in the pair.
     */
    public U getFirst() {
        return this.first;
    }

    /**
     * Returns the second element in the pair.
     */
    public V getSecond() {
        return this.second;
    }
}
