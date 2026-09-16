package brucli.task;

import java.time.LocalDateTime;

/**
 * Represents a task tracked by BruCLI.
 */
public abstract class Task {
    protected int id;
    protected final String description;
    protected boolean isDone;

    /**
     * Creates an incomplete task with its internal ID and description.
     */
    protected Task(int id, String description) {
        this.id = id;
        this.description = description;
        isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmarkDone() {
        isDone = false;
    }

    /**
     * Returns the representation written to the task data file.
     */
    public abstract String serialize();

    /**
     * Returns the one-letter task type displayed to the user.
     */
    protected abstract String getType();

    /**
     * Checks whether this task satisfies an optional date filter.
     */
    public boolean matches(ListFilter filter) {
        if (filter == null) {
            return true;
        }

        LocalDateTime dateTime = getDateTime();
        if (dateTime == null) {
            return false;
        }

        return switch (filter.type()) {
            case BEFORE -> !dateTime.isAfter(filter.boundary());
            case AFTER -> dateTime.isAfter(filter.boundary());
        };
    }

    /**
     * Returns the date used for filtering, or {@code null} for undated tasks.
     */
    protected LocalDateTime getDateTime() {
        return null;
    }

    /**
     * Returns the completion marker displayed in task listings.
     */
    private String getStatus() {
        return isDone ? "X" : " ";
    }

    @Override
    public String toString() {
        return String.format("[%s][%s] %s", getType(), getStatus(), description);
    }
}
