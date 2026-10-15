package zinc.task;

/**
 * Represents a task with a description and completion status.
 */
public abstract class Task {
    /** The description of this task. */
    private final String description;
    /** Whether this task has been completed. */
    private boolean isCompleted;

    /**
     * Creates a new task.
     *
     * @param description The description of the task.
     */
    Task(String description) {
        assert description != null && !description.isBlank() : "Task description must not be blank";
        this.description = description;
        this.isCompleted = false;
    }

    /**
     * Completes the task.
     */
    void markAsCompleted() {
        this.isCompleted = true;
    }

    /**
     * Marks the task as incomplete.
     */
    void markAsIncomplete() {
        this.isCompleted = false;
    }

    /**
     * Checks whether this task is complete.
     *
     * @return {@code true} if the task is complete; otherwise, {@code false}.
     */
    boolean isCompleted() {
        return this.isCompleted;
    }

    /** Returns the task description. */
    public String getDescription() {
        return description;
    }

    /**
     * Returns a display-friendly representation of this task and its status.
     *
     * @return {@code [X]} followed by the task name when complete, or
     *         {@code [ ]} followed by the task name when incomplete.
     */
    @Override
    public String toString() {
        String completionMarker = isCompleted ? "[X] " : "[ ] ";
        return completionMarker + description;
    }

    /**
     * Returns this task's category.
     *
     * @return The type of this task.
     */
    public abstract TaskType getTaskType();
}
