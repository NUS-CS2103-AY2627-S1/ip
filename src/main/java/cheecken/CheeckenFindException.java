package cheecken;

/**
 * Reports that a find command did not include a search keyword.
 */
public class CheeckenFindException extends RuntimeException {
    /**
     * Creates an exception with guidance for the invalid command.
     */
    public CheeckenFindException() {
        super("Please provide a keyword to search for.");
    }
}
