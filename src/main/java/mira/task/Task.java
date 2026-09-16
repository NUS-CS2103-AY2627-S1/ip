package mira.task;

/**
 * Base representation of a task with a description and completion status.
 */
public class Task {
    private final String description;
    private final TaskType type;
    private boolean isDone;

    /**
     * Creates an incomplete task.
     *
     * @param description human-readable task description.
     * @param type kind of task being created.
     */
    public Task(String description, TaskType type) {
        this.description = description;
        this.type = type;
        this.isDone = false;
    }

    /**
     * Returns the task description exactly as stored.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the kind of this task.
     *
     * @return the task type.
     */
    public TaskType getType() {
        return type;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if the task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Updates whether this task has been completed.
     *
     * @param isDone new completion status.
     */
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * Returns details appended after the task description.
     * Subclasses override this when they carry scheduling information.
     *
     * @return formatted scheduling details, or an empty string.
     */
    protected String getDetails() {
        return "";
    }

    /**
     * Returns the completion marker displayed inside a task.
     *
     * @return {@code X} for a completed task, or a space otherwise.
     */
    private String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the task's type, status, description, and scheduling details.
     *
     * @return User-facing task text.
     */
    @Override
    public String toString() {
        return "[" + type.getSymbol() + "][" + getStatusIcon() + "] "
                + description + getDetails();
    }
}
