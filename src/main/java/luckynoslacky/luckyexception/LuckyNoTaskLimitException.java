package luckynoslacky.luckyexception;

/**
 * Indicates that a new task cannot be added because the task list is full.
 */
public class LuckyNoTaskLimitException extends RuntimeException {
    /**
     * Creates a task-limit error with a user-facing message.
     *
     * @param message explanation of the task-limit error
     */
    public LuckyNoTaskLimitException(String message) {
        super(message);
    }
}
