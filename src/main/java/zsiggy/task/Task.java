package zsiggy.task;

/**
 * Represents a task stored by Zsiggy.
 */
public class Task implements Cloneable {
    private String description;
    private boolean isDone;

    /**
     * Creates a new incomplete task.
     *
     * @param description the description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon representing whether the task is completed.
     *
     * @return X if completed, otherwise a blank space
     */
    public String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    /**
     * Returns the task description.
     *
     * @return the task description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Checks whether the task has been completed.
     *
     * @return true if the task is completed
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Marks the task as completed.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Marks the task as incomplete.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns the textual representation of the task.
     *
     * @return the task status and description
     */
    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.description;
    }

    /**
     * Copies this task; descriptions and dates are immutable values.
     */
    public Task copy() {
        try {
            return (Task) clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
