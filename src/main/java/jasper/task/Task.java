package jasper.task;

/**
 * Represents a generic task with a description and completion status.
 */
public class Task {
    /** Description of the task */
    private final String description;
    /** Completion status of the task */
    private boolean isDone;

    /**
     * Constructs a new Task with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        markUndone();
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns a string icon representing whether a task is completed.
     */
    private String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks the task as completed.
     */
    public void markDone() {
        isDone = true;
    }

    /**
     * Marks the task as not completed.
     */
    public void markUndone() {
        isDone = false;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", getStatusIcon(), description);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Task other) {
            return this.description.equals(other.description);
        }
        return false;
    }
}
