package amy.task;

/**
 * Represents one task in Amy's in-memory task list.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        // Every task needs text because descriptions are used in display and persistence.
        assert description != null && !description.isBlank() : "Task description must not be blank";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon used when displaying this task.
     *
     * @return {@code X} for a completed task, or a space otherwise
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
    public void unmarkAsDone() {
        isDone = false;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return true if this task is complete
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns this task's type marker.
     *
     * @return the task type marker
     */
    public String getTypeIcon() {
        return "T";
    }

    /**
     * Returns this task's display text.
     *
     * @return the task description
     */
    public String getDisplayText() {
        return description;
    }

    /**
     * Returns the complete task marker and description for output.
     *
     * @return the task's type, done status, and display text
     */
    public String getFullDisplayText() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + getDisplayText();
    }

    /** Creates a copy of this task, preserving its completion state. */
    public Task copy() {
        Task copy = new Task(description);
        if (isDone) {
            copy.markAsDone();
        }
        return copy;
    }

}
