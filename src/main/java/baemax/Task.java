package baemax;

/**
 * Represents one task stored by the Baemax chatbot.
 */
public class Task {
    /** The text entered by the user for this task. */
    private String description;

    /** Whether the user has marked this task as done. */
    private boolean isDone;

    /**
     * Creates a new task that starts in the not-done state.
     *
     * @param description the task text
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon used when displaying this task.
     *
     * @return {@code X} for a done task or a space for a pending task
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the description text this task was created with.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Formats this task with its status icon and description.
     *
     * @return the task display text
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns an independent copy of this task, for snapshotting the task
     * list before a command that might need to be undone. Subtypes override
     * this to return a copy of their own concrete type.
     *
     * @return a new task with the same description and done state
     */
    public Task copy() {
        Task copy = new Task(description);
        copyDoneStatusInto(copy);
        return copy;
    }

    /**
     * Applies this task's done status to another task, for use by subtype
     * {@code copy()} overrides.
     *
     * @param target the task to update
     */
    protected final void copyDoneStatusInto(Task target) {
        if (isDone) {
            target.markAsDone();
        }
    }

    /**
     * Encodes the fields shared by every task for the save file as
     * {@code <status> | <description>}, where status is {@code 1} when the
     * task is done and {@code 0} otherwise. Subtypes prepend a type tag and
     * append their own fields.
     *
     * @return the save-file representation of this task
     */
    public String toFileFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }
}
