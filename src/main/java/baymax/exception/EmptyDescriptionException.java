package baymax.exception;

/**
 * Represents a task command with no task description.
 */
public class EmptyDescriptionException extends BaymaxException {

    /**
     * Creates an exception for a missing description of the given task type.
     *
     * @param task the task type whose description is missing
     */
    public EmptyDescriptionException(String task) {
        super("     OOPS!!! The description of a " + task + " cannot be empty.");
    }
}
