package leo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be done by a specific date and time.
 */
public class Deadline extends Task {
    /** The date/time this task is due by. */
    private LocalDateTime by;
    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm");

    /**
     * Returns "AM" or "PM" for the given date/time, computed directly
     * from the hour rather than relying on {@link DateTimeFormatter}'s
     * locale-dependent "a" pattern, whose letter case can vary between
     * JDK versions.
     *
     * @param dateTime the date/time to check
     * @return "AM" if before noon, "PM" otherwise
     */
    private static String amOrPm(LocalDateTime dateTime) {
        return dateTime.getHour() < 12 ? "AM" : "PM";
    }

    /**
     * Creates a new, not-done deadline.
     *
     * @param description description of the deadline
     * @param by due date/time, in "yyyy-MM-dd HHmm" format
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = LocalDateTime.parse(by, INPUT_FORMAT);
    }

    /**
     * Returns the type code for a deadline.
     *
     * @return "D"
     */
    @Override
    public String getType() {
        return "D";
    }

    /**
     * Returns this deadline encoded as a save-file line, with the due
     * date/time appended after the fields from {@link Task}.
     *
     * @return the save-file line for this deadline
     */
    @Override
    public String toFileString() {
        return super.toFileString()
                + " | " + by.format(INPUT_FORMAT);
    }

    /**
     * Returns this deadline formatted for display, including the due
     * date/time, e.g. "[D][ ] return book (by: Dec 01 2019, 6:00PM)".
     *
     * @return the display string for this deadline
     */
    @Override
    public String toString() {
        return super.toString()
                + " (by: " + by.format(OUTPUT_FORMAT) + amOrPm(by) + ")";
    }
}
