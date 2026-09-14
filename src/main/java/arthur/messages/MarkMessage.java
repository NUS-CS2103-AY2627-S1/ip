package arthur.messages;

import arthur.tasks.Task;

/**
 * Confirms that a task has been marked as completed.
 */
public class MarkMessage extends Message {
    // AI-assisted: Replaced the magic response format with a named constant.
    private static final String MARK_CONFIRMATION_FORMAT = "%s\n\nmarked:\n%s";

    // AI-assisted: Added the confirmation message for marking a task.
    private final Task task;

    public MarkMessage(Task task) {
        this.task = task;
    }

    @Override
    public String toString() {
        assert task != null : "Task should not be null";
        return String.format(MARK_CONFIRMATION_FORMAT, getRandomQuote(), task);
    }

    @Override
    public boolean isFinal() {
        return false;
    }
}
