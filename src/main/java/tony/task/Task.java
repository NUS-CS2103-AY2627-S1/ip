package tony.task;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Represents a task that can be completed or left incomplete.
 */
public abstract class Task {
    /** Maximum number of characters allowed in a task description. */
    public static final int MAX_DESCRIPTION_LENGTH = 500;

    /** The text describing what must be done. */
    private final String description;

    /** Whether this task has been completed. */
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the task.
     */
    public Task(String description) {
        if (description == null) {
            throw new IllegalArgumentException("A task description cannot be null");
        }
        if (description.isBlank()) {
            throw new IllegalArgumentException("A task description cannot be blank");
        }
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException("A task description is too long");
        }
        if (description.codePoints().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("A task description cannot contain control characters");
        }
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns whether another task has the same type and user-entered details.
     * Completion status is deliberately excluded because marking a task does not make it a new task.
     *
     * @param other task to compare against.
     * @return whether the tasks represent the same matter.
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && description.equalsIgnoreCase(other.description);
    }

    /**
     * Returns the icon used to show this task's completion status.
     *
     * @return {@code X} when complete, or a space when incomplete.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return whether the task is complete.
     */
    public boolean isDone() {
        return isDone;
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Returns whether this task's description contains a keyword, ignoring letter case.
     *
     * @param keyword text to search for in the description
     * @return whether the keyword occurs in the description
     */
    public boolean descriptionContains(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns this task's category.
     *
     * @return the task's type.
     */
    protected abstract TaskType getTaskType();

    /**
     * Returns whether this task falls on the specified date.
     * Tasks without a date return {@code false}.
     *
     * @param date the date to check.
     * @return whether this task falls on the date.
     */
    public boolean isOnDate(LocalDate date) {
        return false;
    }

    /**
     * Formats this task as one line for storage on disk.
     *
     * @return the task type, completion status, and description.
     */
    public String toDataString() {
        return getTaskType().getIcon() + " | " + (isDone ? "1" : "0") + " | "
                + escapeDataField(description);
    }

    /**
     * Escapes characters that have a special meaning in the storage format.
     *
     * @param value a task field to store.
     * @return the field with backslashes and pipe characters escaped.
     */
    private String escapeDataField(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Formats this task for display in the task list.
     *
     * @return the type icon, status icon, and description of this task.
     */
    @Override
    public String toString() {
        return "[" + getTaskType().getIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
