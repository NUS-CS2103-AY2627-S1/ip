package melodie.task;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    private final String description;
    private boolean isCompleted;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isCompleted = false;
    }

    /**
     * Marks this task as completed.
     */
    public void mark() {
        this.isCompleted = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmark() {
        this.isCompleted = false;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isCompleted() {
        return this.isCompleted;
    }

    /**
     * Returns the numeric completion status used by the storage file.
     *
     * @return {@code "1"} if completed, or {@code "0"} otherwise.
     */
    public String getStatusCode() {
        return this.isCompleted ? "1" : "0";
    }

    /**
     * Returns this task in the format used by the storage file.
     *
     * @return Storage-file representation of this task.
     */
    public String toStorageString() {
        return this.getStatusCode() + " | " + this.description;
    }

    @Override
    public String toString() {
        String mark = " ";
        if (this.isCompleted) {
            mark = "X";
        }
        return "[" + mark + "] " + this.description;
    }
}
