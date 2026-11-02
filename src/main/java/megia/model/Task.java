package megia.model;

import java.time.LocalDate;

/**
 * Represents a task description and its completion state.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this(description, false);
    }

    /**
     * Creates a task with the specified description and completion status.
     *
     * @param description Description of the task.
     * @param isDone Completion status of the task.
     */
    public Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns whether this task occurs on the specified date.
     * Tasks without a date, such as todos, do not occur on any date.
     *
     * @param date Date to check.
     * @return False because a generic task has no date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns whether this task's description contains the specified text.
     *
     * @param query Text to search for.
     * @return True if the description contains the query as a literal substring, including case.
     */
    public boolean hasDescriptionContaining(String query) {
        return description.contains(query);
    }

    @Override
    public String toString() {
        return String.format("[%c] %s", (isDone ? 'X' : ' '), description);
    }

    /**
     * Returns this task's fields in their comma-delimited storage representation.
     *
     * @return Completion status and description as stored on disk.
     */
    public String encode() {
        return String.format("%s,%s", isDone, encodeField(description));
    }

    /**
     * Encodes one field for the task storage CSV format.
     *
     * @param value Field value to encode.
     * @return CSV-safe field value.
     */
    protected static String encodeField(String value) {
        if (!value.contains(",") && !value.contains("\"")) {
            return value;
        }
        return '"' + value.replace("\"", "\"\"") + '"';
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return True when the task is complete.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task description.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }
}
