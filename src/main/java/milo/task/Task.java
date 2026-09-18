package milo.task;

/**
 * Represents a single task that Milo keeps track of.
 * A task consists of a description and a flag recording whether it is done.
 */
public class Task {
    /** Separates the fields of a task when it is written to the save file. */
    protected static final String FILE_SEPARATOR = " | ";

    /** What the user wants to do. */
    protected String description;

    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates a task, which always starts out not done.
     *
     * @param description what the task is about
     */
    public Task(String description) {
        // Parser rejects an empty description for every task type, and Storage
        // rejects a save line whose description field is empty, so no caller
        // can reach here without a real description.
        assert description != null : "task description should never be null";
        assert !description.isEmpty() : "callers validate the description before constructing a task";

        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon representing this task's completion status.
     *
     * @return "X" if the task is done, a single space otherwise
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** Marks this task as done. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not done yet. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the completion flag used in the save file, kept separate from
     * {@link #getStatusIcon()} so the on-screen look can change without
     * invalidating already-saved files.
     *
     * @return "1" if the task is done, "0" otherwise
     */
    public String getStatusFlag() {
        return (isDone ? "1" : "0");
    }

    /**
     * Returns this task as one line of the save file. Subclasses prepend
     * their type letter and append whatever extra fields they carry, the
     * same way {@link #toString()} is built up.
     *
     * @return the shared middle of the line: status flag and description
     */
    public String toFileFormat() {
        return getStatusFlag() + FILE_SEPARATOR + description;
    }

    /**
     * Returns an independent copy of this task, carrying the same description
     * and completion status. Undo relies on this: mark and unmark change a
     * task in place, so a snapshot that shared tasks with the live list would
     * change along with it and have nothing to restore.
     *
     * @return a task equal to this one but sharing no state with it
     */
    public Task copy() {
        Task copy = new Task(description);
        copy.isDone = this.isDone;
        return copy;
    }

    /**
     * Returns this task's description.
     *
     * @return the description supplied when the task was created
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the task rendered with its status box in front of the
     * description, for example {@code [X] read book}.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
