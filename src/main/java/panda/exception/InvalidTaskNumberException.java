package panda.exception;

/**
 * Signals that a numbered command does not identify an existing task.
 *
 * Converts missing, non-numeric, and out-of-range task numbers
 * into clear user-facing errors.
 */
public class InvalidTaskNumberException extends PandaException {
    /**
     * Creates an error for a missing or non-numeric task number.
     *
     * @param command the command that requires a number.
     */
    public InvalidTaskNumberException(String command) {
        super("OOPS!!! This panda needs a valid task number after " + command + ".");
    }

    /**
     * Creates an error for a number outside the current task list.
     *
     * @param taskNumber the unavailable one-based task number.
     */
    public InvalidTaskNumberException(int taskNumber) {
        super("OOPS!!! This panda cannot find task " + taskNumber + " in the bamboo stack.");
    }
}
