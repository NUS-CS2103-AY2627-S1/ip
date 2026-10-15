package surveyprogram.object;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a given date or time.
 */
public class Deadline extends Item {
    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);

    private final LocalDateTime by;

    /**
     * Creates a task that must be completed by a specified date-time.
     *
     * @param description deadline description
     * @param by due date-time
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /** Creates a deadline restored with a stable identifier. */
    public Deadline(String id, String description, LocalDateTime by) {
        super(id, description);
        this.by = by;
    }

    /**
     * Returns the deadline's display form, including its due date and completion state.
     *
     * @return formatted deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(OUTPUT_FORMATTER).replace("12:00 AM", "0:00") + ")";
    }

    /**
     * Returns the due date-time.
     *
     * @return due date-time
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Checks whether the deadline falls on a specified date.
     *
     * @param date date to check
     * @return {@code true} when the deadline is due on the date
     */
    @Override
    public boolean inRange(LocalDate date) {
        return by.toLocalDate().equals(date);
    }
}
