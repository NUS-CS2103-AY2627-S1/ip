package jarvis;

/** Represents a task and whether it has been completed. */
public class Task {
    /** Text shown to the user for this task. */
    private final String description;
    /** Completion state used when displaying and saving this task. */
    private TaskStatus status;

    /**
     * Creates a new incomplete task.
     *
     * @param description the task description.
     */
    public Task(String description) {
        assert description != null : "Task description must not be null";
        this.description = description;
        this.status = TaskStatus.NOT_DONE;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        status = TaskStatus.DONE;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        status = TaskStatus.NOT_DONE;
    }

    /** Restores the completion status of a task loaded from storage. */
    void restoreStatus(boolean isDone) {
        assert status != null : "Task status must be initialized before restoring it";
        status = isDone ? TaskStatus.DONE : TaskStatus.NOT_DONE;
    }

    /**
     * Returns the status icon used when displaying this task.
     *
     * @return {@code X} for a done task, otherwise a blank space.
     */
    public String getStatusIcon() {
        return status == TaskStatus.DONE ? "X" : " ";
    }

    /**
     * Returns the task description.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the one-letter marker used for this task type.
     *
     * @return {@code T} for a basic task.
     */
    protected String getTypeIcon() {
        return "T";
    }

    /**
     * Returns the task in the format used by the command-line interface.
     *
     * @return the type marker, completion marker, and description.
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
