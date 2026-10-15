package sage.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be done by a specific date and time.
 */
public class Deadline extends Task {
    /**
     * Due date and time, or {@code null} if no parsed value is available.
     */
    protected LocalDateTime by;

    /**
     * Text retained as the deadline's stored representation.
     */
    protected String byText;

    /**
     * Creates a deadline from a user-provided date string.
     *
     * @param description the task description.
     * @param by the deadline value as text.
     */
    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        this.byText = by == null ? "" : by.trim();
        this.by = parseDateTime(this.byText);
    }

    /**
     * Creates a deadline from a Java LocalDateTime value.
     *
     * @param description the task description.
     * @param by the deadline time.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
        this.byText = by == null ? "" : by.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Returns the due date and time.
     *
     * @return the deadline timestamp, or {@code null} if none is available.
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns the stored textual representation of the deadline.
     *
     * @return the stored deadline text.
     */
    public String getByText() {
        return byText;
    }

    /**
     * Parses a date or date-time string into a LocalDateTime value.
     *
     * @param rawValue the user-entered deadline text.
     * @return the parsed time, or {@code null} if the input is not in a supported format.
     */
    public static LocalDateTime parseDateTime(String rawValue) {
        return TaskDateTime.parse(rawValue);
    }

    /**
     * Formats the task as a string suitable for terminal display.
     *
     * @return the user-facing description of the deadline task.
     */
    @Override
    public String toString() {
        return "[" + type.getSymbol() + "][" + getStatusIcon() + "] " + description
                + " (by: " + TaskDateTime.format(by, byText) + ")";
    }
}
