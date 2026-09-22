package panda.exception;

/**
 * Signals that a find command has no search keyword.
 */
public class EmptySearchTermException extends PandaException {
    /**
     * Creates the standard error for a missing find keyword.
     */
    public EmptySearchTermException() {
        super("OOPS!!! This panda needs a search keyword after find.");
    }
}
