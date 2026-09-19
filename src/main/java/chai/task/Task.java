package chai.task;

import java.time.LocalDate;
import java.util.Locale;

/**
 * A task that can be tracked in the task list.
 *
 * <p>Subclasses provide the type-specific part of a task's display text.</p>
 */
public abstract class Task {
    /** The text describing this task. */
    protected String description;

    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Text describing the task.
     */
    public Task(String description) {
        assert description != null : "Task description must not be null";
        assert !description.isBlank() : "Task description must not be blank";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the completion marker used when displaying this task.
     *
     * @return {@code [X]} when completed, or {@code [ ]} otherwise.
     */
    public String getStatusIcon() {
        return "[" + (this.isDone ? "X" : " ") + "]";
    }

    /**
     * Returns the text describing this task.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if this task is completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether this task's description contains the given keyword.
     * Matching is case-insensitive.
     *
     * @param keyword Keyword to search for.
     * @return {@code true} if the description contains the keyword.
     */
    public boolean containsKeyword(String keyword) {
        String normalizedDescription = description.toLowerCase(Locale.ROOT);
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return normalizedDescription.contains(normalizedKeyword);
    }

    /**
     * Returns the date used to order this task chronologically.
     * Tasks without dates sort after all dated tasks.
     *
     * @return Relevant task date, or the latest supported date for undated tasks.
     */
    public LocalDate getSortDate() {
        return LocalDate.MAX;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsUndone() {
        this.isDone = false;
    }

    @Override
    public String toString() {
        return this.getStatusIcon() + " " + this.description;
    }
}
