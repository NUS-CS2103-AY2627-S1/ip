package epi.task;

import java.time.LocalDateTime;

/** Represents a generic task with a description and completion state. */
public class Task {
    /** Description shown to the user and stored in the task file. */
    protected String description;
    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Task description, preserved without trimming.
     * @throws IllegalArgumentException If the description is null, blank, or contains
     *     a pipe character, a newline, or a carriage return.
     */
    public Task(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Meow! A task needs a description.");
        }
        if (description.contains("|") || description.contains("\n") || description.contains("\r")) {
            throw new IllegalArgumentException("Meow! Task descriptions must stay on one line and cannot contain '|'.");
        }
        this.description = description;
        this.isDone = false;
    }

    /**
     * Copies a valid task's description and completion state.
     *
     * @param original Non-null task to copy without modifying it.
     */
    protected Task(Task original) {
        this.description = original.description;
        this.isDone = original.isDone;
    }

    /**
     * Returns an independent task snapshot for a change that has not yet been saved.
     *
     * @return A new task with the same description and completion state.
     */
    public Task copy() {
        return new Task(this);
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsUndone() {
        this.isDone = false;
    }

    /**
     * Returns whether this task is completed.
     *
     * @return {@code true} if the task is done; otherwise {@code false}.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the status marker used when displaying this task.
     *
     * @return {@code "X"} for a completed task, or a single space for an incomplete task.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Returns the date/time used to order this task chronologically.
     *
     * @return The relevant date/time, or {@code null} for an undated task.
     */
    public LocalDateTime getSortDate() {
        return null;
    }

    /**
     * Returns the common storage fields, without a task-type prefix.
     * Subclasses add their type and any date/time fields to form a complete record.
     *
     * @return Completion status ({@code 1} or {@code 0}) and description, separated by {@code " | "}.
     */
    public String toFileFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Returns the user-facing representation of this task.
     *
     * @return The status marker in brackets followed by the description.
     */
    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + description;
    }
}
