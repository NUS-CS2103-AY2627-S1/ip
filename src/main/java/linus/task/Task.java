package linus.task;

/**
 * Represents a task with a completion status and a description.
 */
public abstract class Task {
    private boolean isDone;
    private String description;

    /**
     * Creates a new Task
     *
     * @param isDone Whether the task has been marked as completed.
     * @param description Description of the task.
     */
    public Task(boolean isDone, String description) {
        this.isDone = isDone;
        this.description = description;
    }

    /**
     * Marks the task as complete by updating the completion status
     * as true.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Marks the task as incomplete by updating the completion
     * status as false.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns a String representation of each Task that
     * is formatted for ease of storing into the storage file.
     *
     * @return Formatted String representation of Event task.
     */
    public abstract String toFileFormat();

    /**
     * Returns the completion status of the Task.
     *
     * @return Whether the Task has been marked as completed.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Returns the description of the Task.
     *
     * @return String Description of the Task.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the String representation of a Task.
     *
     * @return String representation of a Task.
     */
    @Override
    public String toString() {
        if (isDone) {
            return "[X] " + this.description;
        }
        return "[ ] " + this.description;
    }
}
