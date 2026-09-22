package panda.exception;

/**
 * Signals that an update command is malformed or incompatible with a task.
 */
public class InvalidUpdateException extends PandaException {
    /**
     * Creates the standard error explaining the supported update syntax.
     */
    public InvalidUpdateException() {
        super("OOPS!!! This panda needs a valid update. "
                + "Try /name <new name>, /by <date and time>, "
                + "or /from <start> /to <end>.");
    }

    /**
     * Creates an update error with a specific user-facing message.
     */
    private InvalidUpdateException(String message) {
        super(message);
    }

    /**
     * Creates an error for a timing update that does not apply to a task type.
     *
     * @return an error explaining that the selected task cannot be rescheduled.
     */
    public static InvalidUpdateException createForUnsupportedTiming() {
        return new InvalidUpdateException(
                "OOPS!!! This panda cannot apply that timing update to this task type.");
    }
}
