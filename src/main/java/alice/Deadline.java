package alice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task with a description and a deadline date/time by which
 * it must be completed.
 */
public class Deadline extends Task {
    // The locale is pinned because the day period is rendered differently
    // depending on it - "6:00 PM" under en, but "6:00 pm" under en-SG - so
    // without this the same task reads differently on different machines,
    // and the CI job produces different output on each of its three
    // platforms.
    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy, h:mm a", Locale.ENGLISH);

    private LocalDateTime by;

    /**
     * Constructs a Deadline task.
     *
     * @param description description of the task.
     * @param by the date and time by which the task must be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        assert by != null : "deadline date/time must not be null";
        this.by = by;
    }

    /**
     * Returns the deadline date/time of this task.
     *
     * @return the deadline as a LocalDateTime.
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Pushes this deadline's date/time back by the given number of days.
     *
     * @param days the number of days to push the deadline back by; must be positive.
     */
    public void snooze(int days) {
        assert days > 0 : "days to snooze by must be positive";
        this.by = this.by.plusDays(days);
    }

    /**
     * Returns the display name of this task type.
     *
     * @return "Deadline".
     */
    @Override
    public String getTypeName() {
        return "Deadline";
    }

    /**
     * Returns when this task is due, formatted for display.
     *
     * @return the due date, prefixed with "by".
     */
    @Override
    public String getScheduleSummary() {
        return "by " + by.format(OUTPUT_FORMAT);
    }

    /**
     * Returns a string representation of this deadline task, including its
     * type marker, completion status, description, and formatted deadline.
     *
     * @return formatted string for display to the user.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(OUTPUT_FORMAT) + ")";
    }

    /**
     * Returns a string representation of this task suitable for saving to
     * the data file, using ISO date-time format for the deadline.
     *
     * @return formatted string for file storage.
     */
    @Override
    public String toFileFormat() {
        return "D | " + (isDone() ? "1" : "0") + " | " + getDescription() + " | " + by;
    }
}
