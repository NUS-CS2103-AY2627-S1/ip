package benji;

/**
 * Represents a general task in the BENJI task manager.
 *
 * <p>A task has a description and can be marked as either
 * completed or incomplete.</p>
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a new task with the given description.
     *
     * @param description text describing the task
     */
    public Task(String description) {
        assert description != null && !description.isBlank() : "Every task should have a meaningful description";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task is completed.
     *
     * @return true if this task is completed
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the status icon representing whether the task is completed.
     *
     * @return "X" if the task is completed, or a blank space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as completed.
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
     * Returns a string representation of this task.
     *
     * @return the task's status icon followed by its description.
     */
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }


}
