package bot.task;

import java.time.LocalDate;

/**
 * Represents a task with a description and a done/not-done status.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a new, not-done task with the given description.
     *
     * @param description The task's description text.
     */
    public Task(String description) {
        // Every current caller (Parser's parseTodo/parseDeadline/parseEvent
        // for user commands, Storage's parseLine for saved lines) always
        // passes a real String from String.split/substring/trim, which is
        // never null - so a null description here would mean a future
        // caller is constructing a Task directly with bad data.
        assert description != null : "a Task's description must not be null";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the single-character icon shown for this task's done status.
     *
     * @return {@code "X"} if the task is done, a single space otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** The task's description text, e.g. "return book". */
    protected String getDescription() {
        return description;
    }

    /** Whether the task is currently marked done, e.g. for a subclass building its save-file line. */
    protected boolean isDone() {
        return isDone;
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
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns this task's status icon and description, e.g. {@code "[X] read book"}.
     * Subclasses prepend their own type icon to this.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns this task's representation for the data file, as a single
     * pipe-separated line (e.g. {@code "T | 1 | read book"}). Each
     * subclass supplies its own type letter and any extra fields.
     */
    public abstract String toSaveFormat();

    /**
     * Returns whether this task falls on the given calendar date, used by
     * the {@code on} command. A task with no date attached (a Todo) never
     * matches; Deadline and Event override this with their own dates.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the date this task should be ordered by for the
     * {@code sort} command, or {@code null} if it has no date (a Todo).
     * Deadline and Event override this with their due/start date.
     */
    protected LocalDate getSortDate() {
        return null;
    }
}
