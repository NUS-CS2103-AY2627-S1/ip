package victoria.exception;

/** Indicates that a task description is empty. */
public class EmptyDescriptionException extends InvalidTaskException {
    /** Creates an empty-description error. */
    public EmptyDescriptionException(String message) {
        super(message);
    }
}
