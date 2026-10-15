package ff15.task;

/**
 * Represents a task that a user wants to keep track of.
 * Each task has a description and a done/not-done status.
 */
public class Task {
    /** What the user typed to describe this task. */
    private final String description;

    /** Whether the user has marked this task as done. */
    private boolean isDone;

    /**
     * Creates a task that starts out not done.
     *
     * @param description what the user typed to describe the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon used to display this task's status: "X" if done, otherwise a blank space.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done again. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns whether this task description contains {@code keyword}, ignoring
     * the difference between upper and lower case so that {@code find Book}
     * still turns up a task described as {@code read book}.
     *
     * @param keyword the text the user is searching for.
     * @return true if the description contains the keyword.
     */
    public boolean hasKeyword(String keyword) {
        return description.toLowerCase().contains(keyword.toLowerCase());
    }

    /**
     * Returns whether {@code other} describes the same task as this one: the same
     * kind, with the same description, and (for the kinds that have them) the
     * same dates. Whether either is done is not part of it, since the user is
     * asking "do I already have this?", not "have I already done this?".
     * Subclasses with dates extend the check; a plain task compares only what it
     * has.
     */
    public boolean isSameAs(Task other) {
        return getClass() == other.getClass() && description.equals(other.description);
    }

    /**
     * Returns whether this task falls within {@code range}. A plain task has no
     * date attached, so it never does; {@link Deadline} and {@link Event} override
     * this with their own answer.
     */
    public boolean occursIn(DateRange range) {
        return false;
    }

    /** Returns this task as shown to the user, e.g. {@code [X] read book}. */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns this task's representation for the save file, e.g. {@code "1 | read book"}.
     * Subclasses prepend a type letter and append any extra fields of their own.
     */
    public String toFileFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }
}
