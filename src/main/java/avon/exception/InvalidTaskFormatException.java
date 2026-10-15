package avon.exception;

/**
 * Signals that a task command does not follow the format required for its type.
 */
public class InvalidTaskFormatException extends AvonException {
    /**
     * Creates a task-format error with the missing detail and a complete example.
     *
     * @param taskType the type of task being entered.
     * @param problem a clear explanation of the incorrect input.
     * @param example a complete example of the expected command.
     */
    public InvalidTaskFormatException(String taskType, String problem, String example) {
        super("This " + taskType + " command is ill-formed.\n"
                + problem + "\n"
                + "Do enter it in this format: " + example);
    }
}
