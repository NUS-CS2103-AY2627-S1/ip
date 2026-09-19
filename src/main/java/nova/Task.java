package nova;

/**
 * Represents a task with a description and a completion status.
 */
public class Task {
    /** The possible states of a task. */
    public enum TaskStatus {
        DONE, NOT_DONE
    }

    private String description;
    private TaskStatus status = TaskStatus.NOT_DONE;

    /**
     * Creates a task with the given description, initially not done.
     *
     * @param description the task description
     */
    public Task(String description) {
        assert description != null : "Task description must not be null";
        this.description = description;
    }

    /**
     * Returns the description of this task.
     *
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns true if this task is done.
     *
     * @return true if the task is done
     */
    public boolean isDone() {
        return this.status == TaskStatus.DONE;
    }

    /**
     * Marks this task as done.
     */
    public void markDone() {
        this.status = TaskStatus.DONE;
    }

    /**
     * Marks this task as not done.
     */
    public void markUndone() {
        this.status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns a display string of this task, e.g. "[X] read book".
     *
     * @return the display string
     */
    @Override
    public String toString() {
        String statusIcon = this.status == TaskStatus.DONE ? "[X]" : "[ ]";
        return statusIcon + " " + this.getDescription();
    }
}
