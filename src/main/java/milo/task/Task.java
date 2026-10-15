package milo.task;

import java.util.Locale;

/** Represents a task description and its completed/not-completed state. */
public abstract class Task {
    /** Text describing the work to be done. */
    private final String description;
    /** Whether this task has been completed. */
    private boolean isDone;

    /** Creates an incomplete task with the given description. */
    public Task(String description) {
        // Parsing and storage restoration supply text; null would break searching and display.
        assert description != null : "A task must have a non-null description";
        this.description = description;
        this.isDone = false;
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
     * Checks whether this task description contains a keyword, ignoring case.
     *
     * @param keyword text to search for
     * @return {@code true} when the description contains the keyword
     */
    public boolean containsKeyword(String keyword) {
        return description.toLowerCase(Locale.ROOT)
                .contains(keyword.toLowerCase(Locale.ROOT));
    }

    /** Returns the description without display markers. */
    public String getDescription() {
        return description;
    }

    /** Returns whether this task has been completed. */
    public boolean isDone() {
        return isDone;
    }

    /** Returns the common task data used by persistent storage. */
    public String storageString() {
        return String.format("%s | %s", isDone ? "1" : "0", this.description);
    }

    /** Returns the task with its completion marker for user-facing output. */
    @Override
    public String toString() {
        String marker = isDone ? "X" : " ";
        return String.format("[%s] %s", marker, description);
    }
}
