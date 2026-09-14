// javadocs created by AI

package baron.exception;

/**
 * Signals that a user referenced an invalid index for a task collection.
 */
public class IndexException extends BaronException {
    /**
     * Creates an index exception for a collection such as tasks.
     *
     * @param collectionName the name of the collection the user accessed.
     */
    public IndexException(String collectionName) {
        super("That " + collectionName + " number points beyond the ledger. Choose an existing entry.");
    }
}
