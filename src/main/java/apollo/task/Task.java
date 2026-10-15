package apollo.task;

/** Represents a task that can be marked as completed and persisted. */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates an incomplete task with the supplied description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Returns the icon indicating whether this task is complete. */
    public String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    /**
     * Sets the completion state of this task.
     *
     * @param isDone {@code true} to mark the task as done, or {@code false} to mark it as undone
     */
    public void markAsDone(boolean isDone) {
        this.isDone = isDone;
    }

    public boolean getIsDone() {
        return this.isDone;
    }

    /**
     * Checks if Task description contains searchText
     *
     * @param searchText the user input which we are checking for
     */
    public boolean containsDescription(String searchText) {
        return this.description.toLowerCase()
                .contains(searchText.toLowerCase());
    }

    /** Returns the single-letter icon identifying this task's type. */
    public abstract String getIcon();

    /** Returns a human-readable representation of this task. */
    public abstract String toString();

    /** Returns this task serialized in Apollo's storage format. */
    public abstract String toFileString();
}
