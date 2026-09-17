package notmarth.model;

/**
 * Represents a task in the chatbot's task list.
 */
public class Task {
    /** The text shown for this task and saved in the archive. */
    protected final String description;

    /** Whether this task has been marked as complete. */
    protected boolean isDone;
    private final TaskType type;

    /**
     * Creates a task that is initially not done.
     *
     * @param description the task description.
     */
    public Task(String description) {
        this(description, TaskType.GENERIC);
    }

    /**
     * Creates a task with a specific task type.
     *
     * @param description the task description.
     * @param type the kind of task.
     */
    protected Task(String description, TaskType type) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Tasks must have a nonblank description.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Tasks must have a task type.");
        }
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    /**
     * Returns the icon representing this task's completion state.
     *
     * @return {@code X} when the task is done, otherwise a blank space.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
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
    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Returns whether this task is complete.
     *
     * @return {@code true} when this task has been marked as done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task's description for persistence.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the task's kind for persistence.
     *
     * @return the task type.
     */
    public TaskType getTaskType() {
        return type;
    }

    /** Checks whether another task has the same type and description. */
    public boolean hasSameDetailsAs(Task other) {
        return other != null && type == other.type && description.equals(other.description);
    }

    /**
     * Returns the task in the format used when displaying it.
     *
     * @return the optional type marker, status icon, and task description.
     */
    @Override
    public String toString() {
        return type.getDisplayMarker() + "[" + getStatusIcon() + "] " + description;
    }
}
