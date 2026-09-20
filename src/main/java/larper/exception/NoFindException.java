package larper.exception;

/**
 * Signals that no tasks matched the find phrase.
 */
public class NoFindException extends LarperException {
    /**
     * Creates an exception for a find command with no matching tasks.
     */
    public NoFindException() {
        super(" Yeah, I got nothing. The receipts do not exist.");
    }
}
