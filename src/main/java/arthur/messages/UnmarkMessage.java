package arthur.messages;

import arthur.tasks.Task;

/**
 * Confirms that a task has been marked as not completed.
 */
public class UnmarkMessage extends Message {
    // AI-assisted: Replaced the magic response format with a named constant.
    private static final String UNMARK_CONFIRMATION_FORMAT = "%s\n\nunmarked:\n%s";

    // AI-assisted: Added the confirmation message for unmarking a task.
    private final Task task;

    /**
     * Creates a confirmation message for unmarking a task.
     *
     * @param task The task that was unmarked.
     */
    public UnmarkMessage(Task task) {
        this.task = task;
        assert task != null : "Task should not be null";
    }

    @Override
    public String toString() {
        return String.format(UNMARK_CONFIRMATION_FORMAT, getRandomQuote(), task);
    }

    @Override
    public boolean isFinal() {
        return false;
    }
}
