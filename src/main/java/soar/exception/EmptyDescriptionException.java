package soar.exception;

/**
 * Signals that a task command does not include a task description.
 */
public class EmptyDescriptionException extends SoarException {
    /**
     * Creates an exception that identifies the task type with the empty description.
     *
     * @param taskType Name of the task type entered by the user.
     */
    public EmptyDescriptionException(String taskType) {
        super("The " + taskType
                + " description is empty. Give it a few words, and it will be ready to soar!");
    }
}
