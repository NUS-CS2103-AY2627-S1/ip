package harold.task;

/**
 * Represents a task that can be marked as done or not done.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Description of the task.
     */
    protected Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as complete.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether the task is complete.
     *
     * @return {@code true} if the task is complete.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the icon that identifies the task type.
     *
     * @return Task type icon.
     */
    public abstract String getTypeIcon();

    /**
     * Returns whether another task has the same type and user-entered details.
     * Completion state is deliberately ignored when detecting duplicate tasks.
     *
     * @param other Task to compare with this task.
     * @return {@code true} if both tasks have the same identifying details.
     */
    boolean hasSameDetails(Task other) {
        return other != null
                && getClass() == other.getClass()
                && description.equals(other.description);
    }

    /**
     * Returns the task formatted with its type, completion status, and description.
     *
     * @return User-facing task representation.
     */
    @Override
    public String toString() {
        String statusIcon = isDone ? "X" : " ";
        return String.format("[%s][%s] %s", getTypeIcon(), statusIcon, description);
    }
}
