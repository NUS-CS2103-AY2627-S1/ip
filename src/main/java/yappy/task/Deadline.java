package yappy.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that should be completed by a specific date or time.
 */
public class Deadline extends Task {
    /** Format used when showing dates to the user. */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** Date by which this task should be completed. */
    private final LocalDate by;

    /**
     * Creates a deadline task with the given description and date.
     */
    public Deadline(String description, LocalDate by) {
        super(TaskType.DEADLINE, description);
        this.by = by;
    }

    /** Returns the date by which this task should be completed. */
    public LocalDate getBy() {
        return by;
    }

    @Override
    public String toDataString() {
        return "D | " + (isDone ? "1" : "0") + " | " + encode(description)
                + " | " + encode(by.toString());
    }

    /**
     * Returns the deadline formatted with its deadline text.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
