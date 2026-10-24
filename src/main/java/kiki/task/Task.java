package kiki.task;

/**
 * Represents a task tracked by Kiki, made up of a description, a done/not-done
 * status, and a {@link TaskType} that determines its display icon.
 */
public class Task {
    private final String description;
    private final TaskType type;
    private boolean isDone;
    private Priority priority;

    /**
     * Creates a new, not-done task.
     *
     * @param description Description of the task.
     * @param type Category of the task, used to pick its display icon.
     */
    public Task(String description, TaskType type) {
        this.description = description;
        this.type = type;
        this.isDone = false;
        this.priority = Priority.NORMAL;
    }

    /**
     * Returns the display icon for this task's completion status.
     *
     * @return {@code X} for a completed task, or a blank space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Checks whether this task has been completed.
     *
     * @return True if the task is complete.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task description.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the category of this task.
     *
     * @return Task category.
     */
    public TaskType getType() {
        return type;
    }

    /**
     * Returns this task's priority.
     *
     * @return Task priority.
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Updates this task's priority.
     *
     * @param priority New priority.
     */
    public void setPriority(Priority priority) {
        assert priority != null : "Task priority should not be null";
        this.priority = priority;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    @Override
    public String toString() {
        return "[" + type.getSymbol() + "][" + getStatusIcon() + "][" + priority.getSymbol() + "] " + description;
    }
}
