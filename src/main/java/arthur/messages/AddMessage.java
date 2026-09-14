package arthur.messages;

import arthur.tasks.Task;

/**
 * Confirms that a task has been added.
 */
public class AddMessage extends Message {
    // AI-assisted: Replaced the magic response format with a named constant.
    private static final String ADD_CONFIRMATION_FORMAT = "%s\n\nadded:\n%s";

    // AI-assisted: Added the response message for a successfully added task.
    private final Task task;

    /**
     * Creates a confirmation message for a task.
     *
     * @param task the task that was added.
     */
    public AddMessage(Task task) {
        this.task = task;
        assert task != null : "Task should not be null";
    }

    @Override
    public String toString() {
        return String.format(ADD_CONFIRMATION_FORMAT, getRandomQuote(), task);
    }

    @Override
    public boolean isFinal() {
        return false;
    }
}
