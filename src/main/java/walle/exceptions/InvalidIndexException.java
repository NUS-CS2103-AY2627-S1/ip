package walle.exceptions;

/**
 * Thrown when a {@code mark}/{@code unmark}/{@code delete}/{@code deletenote}
 * command refers to a position that isn't in the list.
 */
public class InvalidIndexException extends WALLEException {

    /**
     * Creates an InvalidIndexException for the given out-of-range position.
     *
     * @param pos       the 1-based position the user gave.
     * @param size      the number of entries actually in the list.
     * @param entryType the kind of entry being referred to, e.g. {@code "task"} or {@code "note"}.
     */
    public InvalidIndexException(int pos, int size, String entryType) {
        super("\n   ERROR :( " + pos + " isn't a valid " + entryType + " number. You have " + size
                + " " + entryType + "(s).\n");
    }

}
