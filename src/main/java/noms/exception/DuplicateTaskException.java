package noms.exception;

/** Indicates that a task with the same details already exists. */
public class DuplicateTaskException extends NomsException {
    /** Creates an exception with guidance for adding a distinct task. */
    public DuplicateTaskException() {
        super("Noms already has that task on the menu.\n"
                + "Try adding a task with different details.");
    }
}
