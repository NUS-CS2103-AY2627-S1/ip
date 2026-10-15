package slotbot.task;

/**
 * Represents a task that can be marked as done or not done.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an unfinished task.
     *
     * @param description Task description.
     */
    public Task(String description) {
        assert description != null && !description.isBlank() : "Task description must not be blank";

        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as done. */
    public void markDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markUndone() {
        isDone = false;
    }

    /**
     * Returns this task's completion status.
     *
     * @return True if this task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task's description.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the task's display text.
     *
     * @return Task status and description.
     */
    @Override
    public String toString() {
        String status = isDone ? "[X]" : "[ ]";
        return status + " " + description;
    }
}
