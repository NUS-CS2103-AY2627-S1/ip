package cheecken;

/**
 * Reports a delete command without a task index.
 */
public class CheeckenDeleteException extends RuntimeException {
    /**
     * Creates an exception with guidance for the invalid command.
     */
    public CheeckenDeleteException() {
        super("What are you trying to delete?\n(e.g. delete 3)");
    }
}
