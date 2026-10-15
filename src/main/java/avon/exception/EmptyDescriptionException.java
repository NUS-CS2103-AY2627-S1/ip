package avon.exception;

/**
 * Signals that a task command is missing its required description.
 */
public class EmptyDescriptionException extends AvonException {
    /**
     * Creates an error tailored to the task type that lacks a description.
     *
     * @param taskType the task type entered by the user.
     * @param example a complete example of the expected command.
     */
    public EmptyDescriptionException(String taskType, String example) {
        super("The " + taskType + " description cannot be empty.\n"
                + "Do enter it in this format: " + example);
    }
}
