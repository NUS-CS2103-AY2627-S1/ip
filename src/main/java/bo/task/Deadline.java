package bo.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);

    private LocalDate by;

    /**
     * Creates a deadline task.
     *
     * @param description The task description.
     * @param by The deadline date.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the date by which this task is due.
     *
     * @return This deadline's due date.
     */
    public LocalDate getDueDate() {
        return by;
    }

    /**
     * Replaces this deadline's due date.
     *
     * @param newDueDate The replacement due date.
     */
    public void reschedule(LocalDate newDueDate) {
        by = newDueDate;
    }

    /**
     * Formats a date in the style displayed by Bo.
     *
     * @param date The date to format.
     * @return The formatted date.
     */
    public static String formatDate(LocalDate date) {
        return date.format(DISPLAY_DATE_FORMAT);
    }

    /**
     * Returns this deadline in the task-list format.
     *
     * @return The formatted deadline.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + formatDate(by) + ")";
    }

    /**
     * Returns this deadline in the save-file format.
     *
     * @return The saved deadline text.
     */
    @Override
    public String toFileString() {
        return "D | " + getSaveStatus() + " | " + getDescription() + " | " + by;
    }
}
