package jaku.task;

/**
 * Represents a task tracked by Jaku, including its description and completion status.
 */
public class Task {
    /** Description supplied by the user when the task is created. */
    private final String description;

    /** Whether the task has been completed. */
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not completed. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return true when completed
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task description without display formatting.
     *
     * @return the user-provided description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} when done, or a space when not done
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the task in the format shown in Jaku's task list.
     *
     * @return the status icon followed by the task description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
