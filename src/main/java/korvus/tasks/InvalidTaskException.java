package korvus.tasks;

/**
 * Exception that occurs for invalid task operations
 */
public class InvalidTaskException extends Exception {

    /**
     * Returns an instance of InvalidTaskException, if there is any invalid task operations.
     *
     * @param msg Message to be passed down.
     */
    public InvalidTaskException(String msg) {
        super(msg);
    }
}
