package peanutbuttercat;

import java.time.LocalDate;

/**
 * Represents the shared description and completion state of a task.
 */
public class Task {
    private final String description;
    private boolean isDone;
    private LocalDate completionDate;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = requireNonBlank(description, "description");
        this.isDone = false;
        this.completionDate = null;
    }

    /**
     * Validates a required text field shared by all task types.
     *
     * @param value Value to validate.
     * @param fieldName Name used in the exception message.
     * @return The original value with surrounding whitespace removed.
     */
    protected static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Task " + fieldName + " cannot be blank.");
        }
        return value.trim();
    }

    /** Escapes storage delimiters while keeping the legacy file format readable. */
    protected static String escapeStorageField(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        markAsDone(LocalDate.now());
    }

    /**
     * Marks this task as completed on the supplied date if it is currently incomplete.
     *
     * @param completionDate Date on which the task was completed.
     */
    public void markAsDone(LocalDate completionDate) {
        if (completionDate == null) {
            throw new IllegalArgumentException("Task completion date cannot be blank.");
        }
        if (!isDone) {
            isDone = true;
            this.completionDate = completionDate;
        }
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        isDone = false;
        completionDate = null;
    }

    /** Returns whether this task is complete. */
    public boolean isDone() {
        return isDone;
    }

    /** Returns the completion date, or {@code null} when it is unknown or the task is incomplete. */
    public LocalDate getCompletionDate() {
        return completionDate;
    }

    /** Restores completion data without treating loading as a new completion action. */
    void restoreCompletion(boolean isDone, LocalDate completionDate) {
        this.isDone = isDone;
        this.completionDate = isDone ? completionDate : null;
    }

    /**
     * Returns this task in the format used by the on-disk task list.
     *
     * @return A pipe-delimited task record.
     */
    public String toFileString() {
        return appendCompletionDate("T | " + getCompletionState() + " | "
                + escapeStorageField(description));
    }

    /**
     * Returns whether this task is scheduled on the supplied date.
     * Tasks without a schedule do not occur on any date.
     *
     * @param date Date to check against the task's schedule.
     * @return {@code true} if the task occurs on the date, otherwise {@code false}.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns this task's completion state in the storage format.
     *
     * @return {@code 1} when the task is done, otherwise {@code 0}.
     */
    protected String getCompletionState() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns the task description for subclasses that format storage records.
     *
     * @return This task's description.
     */
    protected String getDescription() {
        return description;
    }

    /** Returns whether another task has the same type and user-provided details. */
    boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && normalizeDescription(description).equalsIgnoreCase(normalizeDescription(other.description));
    }

    /** Appends the optional completion date to a task's storage record. */
    protected String appendCompletionDate(String taskRecord) {
        return completionDate == null ? taskRecord : taskRecord + " | " + completionDate;
    }

    private static String normalizeDescription(String value) {
        return value.replaceAll("\\s+", " ");
    }

    private String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns this task in the format shown in the task list.
     *
     * @return A display string containing the completion status and description.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
