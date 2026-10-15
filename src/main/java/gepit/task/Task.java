package gepit.task;

/**
 * Represents a task that can be marked as done or not done.
 */
public class Task {
    private String description;
    private boolean isDone;

    /**
     * Creates a task with the specified description.
     *
     * @param text Description of the task.
     */
    public Task(String text) {
        this.description = text;
    }

    /**
     * Marks this task as done.
     */
    public void markDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markNotDone() {
        this.isDone = false;
    }

    private String getStatusIcon() {
        if (isDone) {
            return "[X]";
        }
        return "[ ]";
    }

    public String getDescription() {
        return this.description;
    }

    /**
     * Updates the description of this task.
     *
     * @param description New task description.
     */
    public void updateDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the persistent representation of this task.
     *
     * @return Task representation used for data storage.
     */
    public String toDataString() {
        return (isDone ? "1" : "0")
                + " | " + this.description;
    }

    @Override
    public String toString() {
        return getStatusIcon() + " " + this.description;
    }
}
