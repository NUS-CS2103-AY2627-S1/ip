package quackers;

/**
 * Represents one task and whether the user has completed it.
 */
public class Task {
    private final TaskType type;
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task of the given type.
     *
     * @param type the kind of task.
     * @param description the work the user wants to remember.
     */
    public Task(TaskType type, String description) {
        this.type = type;
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the kind of this task.
     *
     * @return the task type
     */
    public TaskType getType() {
        return type;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} when the task is done, otherwise a blank space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} when the task is complete
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether this item has a completion status that can be changed.
     *
     * @return {@code true} for actionable tasks
     */
    public boolean supportsCompletionStatus() {
        return true;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not yet completed.
     */
    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Returns the description shared by all task types.
     *
     * @return the task description
     */
    protected String getDescription() {
        return description;
    }

    /**
     * Formats this task for display in command responses and task lists.
     *
     * @return the type, status icon, and task description
     */
    @Override
    public String toString() {
        return "[" + type.getSymbol() + "][" + getStatusIcon() + "] " + description;
    }
}
