package nova.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed before a specified date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private final LocalDate by;

    /**
     * Creates an incomplete deadline.
     *
     * @param description description of the deadline.
     * @param by date by which the task should be completed.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns whether this deadline falls on the given date.
     *
     * @param date date to compare with this deadline.
     * @return {@code true} if this deadline falls on the date.
     */
    public boolean isDueOn(LocalDate date) {
        return by.equals(date);
    }

    /**
     * Returns this deadline in the data-file format.
     *
     * @return serialized deadline
     */
    @Override
    public String toDataString() {
        return "V2 | D | " + getDataStatus() + " | " + encodeDataField(description)
                + " | " + encodeDataField(by.toString());
    }

    /**
     * Returns this deadline in its display format.
     *
     * @return formatted deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
