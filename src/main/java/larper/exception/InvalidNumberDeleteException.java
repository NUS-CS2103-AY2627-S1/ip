package larper.exception;

/**
 * Signals that a delete command used a number outside the current task list.
 */
public class InvalidNumberDeleteException extends LarperException {
    /**
     * Creates an exception that explains the valid delete number range.
     *
     * @param taskCount Number of tasks currently in the list.
     */
    public InvalidNumberDeleteException(int taskCount) {
        super(" Citation needed. That delete number does not exist.\n"
                + " You have " + taskCount + " public commitment(s), so pick from 1 to "
                + taskCount + ".");
    }
}
