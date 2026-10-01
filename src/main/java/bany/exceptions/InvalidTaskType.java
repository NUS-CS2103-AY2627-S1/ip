package bany.exceptions;

/** Indicates that a task has an unsupported type. */
public class InvalidTaskType extends RuntimeException {
    /**
     * Creates an exception with a description of the unsupported type.
     *
     * @param message explanation of the invalid task type.
     */
    public InvalidTaskType(String message) {
        super(message);
    }
}
