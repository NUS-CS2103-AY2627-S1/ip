package kelore.exception;

/** Represents an attempt to add a task whose details already exist. */
public class DuplicateTaskException extends KeloreInputException {
    /** Creates an exception explaining that a matching task already exists. */
    public DuplicateTaskException() {
        super("That task already exists in your list.");
    }
}
