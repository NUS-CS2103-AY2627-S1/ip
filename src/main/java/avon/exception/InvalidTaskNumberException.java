package avon.exception;

/**
 * Signals that a task-number command does not identify an existing task.
 */
public class InvalidTaskNumberException extends AvonException {
    /**
     * Creates a task-number error with guidance tailored to the command.
     *
     * @param commandKeyword the keyword of the task-number command.
     * @param problem a clear explanation of the invalid number.
     */
    public InvalidTaskNumberException(String commandKeyword, String problem) {
        this(commandKeyword, problem, true);
    }

    /**
     * Creates a task-number error, optionally with a valid command example.
     *
     * @param commandKeyword the keyword of the task-number command.
     * @param problem a clear explanation of the invalid number.
     * @param shouldShowExample whether a command example would help resolve the error.
     */
    public InvalidTaskNumberException(String commandKeyword, String problem,
            boolean shouldShowExample) {
        super("I cannot " + commandKeyword + " that task.\n"
                + problem
                + (shouldShowExample
                    ? "\nExample: " + commandKeyword + " 1" : ""));
    }
}
