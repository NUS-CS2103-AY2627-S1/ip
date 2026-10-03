package ramly.exception;

/** Indicates that supplied task data violates a product rule. */
public class TaskValidationException extends RuntimeException {
    /** Creates an exception with an actionable user-facing message. */
    public TaskValidationException(String message) {
        super(message);
    }
}
