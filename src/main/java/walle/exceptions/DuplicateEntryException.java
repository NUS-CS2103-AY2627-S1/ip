package walle.exceptions;

/**
 * Thrown when adding a task or note whose details exactly match one already
 * being tracked.
 */
public class DuplicateEntryException extends WALLEException {

    /**
     * Creates a DuplicateEntryException for the given kind of entry.
     *
     * @param entryType the kind of entry that was duplicated, e.g. {@code "task"} or {@code "note"}.
     */
    public DuplicateEntryException(String entryType) {
        super("\n   ERROR :( You already have a " + entryType + " with the exact same details!\n");
    }

}
