package jason.task;

/**
 * Represents a task with a description and completion state.
 */
public abstract class Task {
    private String description;
    private boolean isCompleted;

    /**
     * Creates an incomplete task with the supplied description.
     *
     * @param description text describing the task.
     */
    public Task(String description) {
        this.description = description;
        isCompleted = false;
    }

    /**
     * Marks this task as complete.
     */
    public void markComplete() {
        this.isCompleted = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markIncomplete() {
        this.isCompleted = false;
    }

    /**
     * Returns the task description for persistence.
     *
     * @return task description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return true if the task is complete; otherwise false.
     */
    public boolean isCompleted() {
        return this.isCompleted;
    }

    /**
     * Returns the completion marker and description for display.
     *
     * @return formatted task description with its completion marker.
     */
    @Override
    public String toString() {
        if (this.isCompleted) {
            return String.format("[X] %s", this.description);
        }
        return String.format("[ ] %s", this.description);
    }
}
