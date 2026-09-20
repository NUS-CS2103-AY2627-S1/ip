package bkxss;

import java.util.Locale;

/**
 * Represents one task and whether it has been completed.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description text that describes the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the symbol used to display this task's completion state.
     *
     * @return {@code "X"} for a completed task, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been marked as completed.
     *
     * @return {@code true} if the task is done, {@code false} otherwise
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Checks whether this task's description contains the supplied keyword.
     * Matching is case-insensitive so searches are convenient for users.
     *
     * @param keyword text to look for in the description
     * @return {@code true} when the description contains the keyword
     */
    public boolean matchesKeyword(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /** Compares task identity without considering its completion status. */
    public boolean hasSameDetails(Task other) {
        if (other == null || getClass() != other.getClass() || !description.equals(other.description)) {
            return false;
        }
        if (this instanceof Deadline deadline && other instanceof Deadline otherDeadline) {
            return deadline.getBy().equals(otherDeadline.getBy());
        }
        if (this instanceof Event event && other instanceof Event otherEvent) {
            return event.getFrom().equals(otherEvent.getFrom()) && event.getTo().equals(otherEvent.getTo());
        }
        return true;
    }

    /** Rejects empty descriptions and characters that cannot round-trip through the storage format. */
    static void validateDescription(String description) throws BkxssException {
        if (description == null || description.isBlank()) {
            throw new BkxssException("a task description cannot be empty.");
        }
        if (description.contains("|") || description.chars().anyMatch(Character::isISOControl)) {
            throw new BkxssException("task descriptions cannot contain | or control characters.");
        }
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not completed. */
    public void markAsNotDone() {
        this.isDone = false;
    }
}
