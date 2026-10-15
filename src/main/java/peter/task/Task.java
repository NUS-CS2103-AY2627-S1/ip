package peter.task;

/**
 * Represents a general task in the task manager.
 * A task has a description and a status indicating whether it is completed.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Constructs a Task object with the specified description.
     * The task is initially not completed.
     *
     * @param description The textual description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon of the task.
     * "X" indicates completed, while a blank space indicates incomplete.
     *
     * @return Status icon as a String.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Returns whether this task is complete.
     *
     * @return True when the task has been marked as done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks the task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks the task as incomplete.
     */
    public void markAsUndone() {
        this.isDone = false;
    }

    public String getDescription() {
        return this.description;
    }

    /**
     * Checks whether another task has the same type and user-provided details.
     * Completion status is intentionally ignored when detecting duplicates.
     *
     * @param other Task to compare.
     * @return True when both tasks represent the same waypoint.
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && description.equalsIgnoreCase(other.description);
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + this.description;
    }

    /**
     * Formats the task details for file storage.
     *
     * @return Formatted string representation for saving to storage.
     */
    public String toFileFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }
}
