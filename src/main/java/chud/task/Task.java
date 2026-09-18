package chud.task;

import java.time.LocalDate;

/**
 * A single task the chatbot is tracking: a description and a done/not-done status, shared by
 * every task type ({@link Todo}, {@link Deadline}, {@link Event}). Subclasses add their own
 * date/time fields and extend the display and save-file formats accordingly.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    /** Creates a new, not-yet-done task with the given description. */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Returns "X" if this task is done, or a blank space otherwise. */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** Returns this task's description. */
    public String getDescription() {
        return description;
    }

    /** Returns true if this task is marked done. */
    public boolean isDone() {
        return isDone;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns this task's type, used for its display tag (e.g. "T") and save-file tag. */
    public abstract TaskType getTaskType();

    /**
     * Returns true if this task is scheduled to occur on the given date, for the {@code on}
     * command. A plain Todo has no date, so the default implementation returns false.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the date this task should be ordered by for the {@code list /sort date} option,
     * or null if this task has no date. A plain Todo has no date, so the default implementation
     * returns null; Deadline and Event override this with their own date field.
     */
    public LocalDate getSortDate() {
        return null;
    }

    /**
     * Returns true if this task's description contains the given keyword, for the {@code find}
     * command. The match is case-insensitive and looks only at the description, not the date/
     * time fields a Deadline or Event may have.
     */
    public boolean matchesKeyword(String keyword) {
        return description.toLowerCase().contains(keyword.toLowerCase());
    }

    /**
     * Encodes this task as a single line for storage in the save file, e.g.
     * {@code "T | 1 | read book"}. Subclasses append their own extra fields.
     */
    public String toFileString() {
        return getTaskType().getTag() + " | " + (isDone ? "1" : "0") + " | " + description;
    }

    @Override
    public String toString() {
        return "[" + getTaskType().getTag() + "][" + getStatusIcon() + "] " + description;
    }
}
