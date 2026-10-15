package atlas.task;

/** Represents a task that Atlas can track. */
public class Task {
    /** The text describing the work to be done. */
    private final String description;

    /** Whether the task has been marked as complete. */
    private boolean isDone;

    /** Priority attached to this task, or {@code null} when none has been set. */
    private Priority priority;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description text describing the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
        this.priority = null;
    }

    /**
     * Returns the status icon used when displaying this task.
     *
     * @return {@code X} for a completed task, or a blank space otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Returns the task description.
     *
     * @return task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether the task has been marked as done.
     *
     * @return {@code true} when the task is complete.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task's priority.
     *
     * @return the attached priority, or {@code null} when none has been set.
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Attaches a priority to this task.
     *
     * @param priority priority to attach.
     */
    public void setPriority(Priority priority) {
        assert priority != null : "a priority to attach must not be null";
        this.priority = priority;
    }

    /** Removes any priority attached to this task. */
    public void clearPriority() {
        this.priority = null;
    }

    /**
     * Returns the bracketed priority tag used when displaying this task.
     *
     * @return the tag, e.g. {@code [HIGH]}, or an empty string when there is no priority.
     */
    public String getPriorityTag() {
        return priority == null ? "" : "[" + priority.getTag() + "]";
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the task in the display format shared by all task types.
     *
     * @return status icon, the priority tag when one is attached, and the
     *     task description.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "]" + getPriorityTag() + " " + description;
    }
}
