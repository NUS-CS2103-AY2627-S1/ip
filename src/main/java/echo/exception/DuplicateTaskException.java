package echo.exception;

/**
 * Signals that a task with the same identifying details already exists.
 */
public class DuplicateTaskException extends EchoException {
    /** Creates the error shown when a duplicate task is added. */
    public DuplicateTaskException() {
        super("That mission is already on the manifest.");
    }
}
