package kpei.exceptions;

/**
 * Signals that an invalid or out-of-bounds task index was specified.
 */
public class InvalidIndexException extends BertException {
    /**
     * Constructs an {@code InvalidIndexException} with the specified detail message.
     *
     * @param index The 1-based index of the item accessed.
     * @param size The number of elements in the list.
     */
    public InvalidIndexException(int index, int size) {
        super(String.format("Cannot access index %d from list of size %d.", index, size));
    }
}
