package fifi.exception;

/**
 * Indicates that a new task repeats the details of an existing task.
 */
public class DuplicateTaskException extends FifiException {
    /**
     * Creates an error explaining why the duplicate task cannot be added.
     *
     * @param message the guidance shown to the user
     */
    public DuplicateTaskException(String message) {
        super(message);
    }
}
