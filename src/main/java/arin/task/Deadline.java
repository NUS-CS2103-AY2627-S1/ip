package arin.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified date and time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate date;

    /**
     * Creates a deadline task with a calendar date.
     *
     * @param description Description of the task.
     * @param date Deadline date.
     */
    public Deadline(String description, LocalDate date) {
        super(description);
        assert date != null : "Deadline dates must be parsed before construction";
        this.date = date;
    }

    /**
     * Returns the calendar date by which this task is due.
     *
     * @return Deadline date.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns this deadline in the storage-file format, including its ISO date.
     *
     * @return Storage text prefixed with the deadline task type and date.
     */
    @Override
    public String storageToString() {
        return "D" + super.storageToString() + " | " + date;
    }

    /**
     * Returns this deadline in the display format, including its formatted date.
     *
     * @return Display text prefixed with the deadline task type and date.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + date.format(DISPLAY_FORMAT) + ")";
    }
}
