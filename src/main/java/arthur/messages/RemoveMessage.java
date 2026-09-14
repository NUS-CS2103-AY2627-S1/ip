package arthur.messages;

import arthur.tasks.Task;

/**
 * Confirms that a task has been removed.
 */
public class RemoveMessage extends Message {
    // AI-assisted: Replaced the magic response format with a named constant.
    private static final String REMOVE_CONFIRMATION_FORMAT = "%s\n\nremoved:\n%s";

    // AI-assisted: Added the confirmation message for marking a task.
    private final Task task;

    /**
     * Creates a confirmation message for removing a task.
     *
     * @param task The task that was removed.
     */
    public RemoveMessage(Task task) {
        this.task = task;
        assert task != null : "Task should not be null";
    }

    @Override
    public String toString() {
        return String.format(REMOVE_CONFIRMATION_FORMAT, getRandomQuote(), task);
    }

    @Override
    public boolean isFinal() {
        return false;
    }
}
