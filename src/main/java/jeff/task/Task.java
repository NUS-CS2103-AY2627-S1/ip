package jeff.task;

import java.time.LocalDate;
import java.util.Optional;

/**
 * A single to-do item with a description and a done/not-done status.
 * {@link Todo}, {@link Deadline}, and {@link Event} extend this with their
 * own extra fields (e.g. dates) and formatting.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a new, not-done task with the given description.
     *
     * @param description what the task is; must not be null or blank.
     * @throws IllegalArgumentException if description is null or blank.
     */
    public Task(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("The description of a task cannot be empty.");
        }
        // toSaveFormat()/Storage#parseLine round-trip tasks through a "|"-delimited save
        // file, so a description containing "|" would silently corrupt that file (and be
        // misread, or dropped, the next time it loads) instead of failing loudly here.
        if (description.contains("|")) {
            throw new IllegalArgumentException("A task description cannot contain the '|' character.");
        }
        this.description = description;
        this.isDone = false;
    }

    /** Returns "X" if this task is done, or a blank space otherwise. */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /** Returns this task's description. */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the date this task should be ordered by when sorting tasks chronologically,
     * or empty if it has no date (e.g. a plain to-do). {@link Deadline} and {@link Event}
     * override this with their own date.
     */
    public Optional<LocalDate> getChronologicalDate() {
        return Optional.empty();
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void unmarkAsDone() {
        isDone = false;
    }

    /** Returns this task's status icon and description, e.g. "[X] read book". */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns this task's data encoded as a single line for {@link jeff.storage.Storage} to
     * write to disk. Subclasses prepend their own type letter and append any
     * extra fields (e.g. dates), separated by {@code " | "}.
     */
    public String toSaveFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }
}
