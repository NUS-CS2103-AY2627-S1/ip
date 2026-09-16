package tt.task;

/**
 * Represents a task and its completion state.
 */
public class Task {
    private final String description;
    private boolean isDone = false;

    /**
     * Creates an incomplete task with the supplied description.
     *
     * @param description description of the task.
     */
    public Task(String description) {
        this.description = description;
    }

    /**
     * Returns the task description.
     *
     * @return task description.
     */
    public String getTask() {
        return description;
    }

    /**
     * Returns {@code X} when complete, otherwise a blank status marker.
     *
     * @return marker representing the completion state.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether the task is complete.
     *
     * @return {@code true} when the task is complete.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks the task as complete.
     */
    public void mark() {
        isDone = true;
    }

    /**
     * Marks the task as incomplete.
     */
    public void unmark() {
        isDone = false;
    }

    /**
     * Returns the task in the format displayed by the user interface.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
