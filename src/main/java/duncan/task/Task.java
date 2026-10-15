package duncan.task;

import duncan.Storage;

/**
 * Represents a task with a description and a done/not-done status.
 * Concrete task types (e.g. {@link Todo}, {@link Deadline}, {@link Event})
 * extend this class to add their own scheduling details.
 */
public abstract class Task {
    /** Separates the fields within one task's line in the save file. */
    public static final String FIELD_SEPARATOR = "\t";

    /** Matches a run of one or more whitespace characters, e.g. "  " in "read  book". */
    private static final String WHITESPACE_RUN = "\\s+";

    protected String description;
    protected boolean isDone;

    /**
     * Creates a task that is not done yet.
     *
     * @param description What the task is, as typed by the user.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Returns "X" if this task is done, or a single space if it is not. */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /** Returns this task's description, as typed by the user. */
    public String getDescription() {
        return description;
    }

    /** Returns whether this task is done. */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether {@code other} is the same task as this one: the same type
     * and the same description, ignoring case, spaces around it, and how many
     * spaces separate its words. Whether either task is done does not matter.
     * Subclasses with dates also require the dates to match.
     *
     * @param other The task to compare with.
     * @return true if the two count as the same task.
     */
    public boolean isDuplicateOf(Task other) {
        return getClass() == other.getClass()
                && normalize(description).equalsIgnoreCase(normalize(other.description));
    }

    /** Returns {@code text} without surrounding whitespace, and with each run of whitespace made one space. */
    private static String normalize(String text) {
        return text.strip().replaceAll(WHITESPACE_RUN, " ");
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns this task as one line of text for the save file, in the
     * format described by {@link Storage}. Each subclass prefixes its own
     * type letter and appends its own date/time fields.
     */
    public abstract String toFileFormat();

    /**
     * Returns the fields every task shares in the save file: the done
     * status followed by the description.
     */
    protected String getSharedFileFields() {
        return (isDone ? "1" : "0") + FIELD_SEPARATOR + description;
    }

    /** Returns this task's status icon and description, e.g. "[X] read book". */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
