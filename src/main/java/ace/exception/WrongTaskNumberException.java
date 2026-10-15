package ace.exception;

/**
 * Thrown when a command that refers to a task by number (e.g. "mark 5" when there
 * are only 4 tasks, or "mark 0") is given a number that isn't a valid, in-range
 * task index.
 */
public class WrongTaskNumberException extends AceException {

    /**
     * Creates a new exception for a task number that doesn't refer to any task
     * currently in the list.
     *
     * @param taskNumber the invalid task number the user gave.
     */
    public WrongTaskNumberException(int taskNumber) {
        super(String.format(
                "Oh no! This card has to be discarded! I do not see the number %d in your hand! "
                        + "Try 'list' to see your current cards.", taskNumber));
    }
}
