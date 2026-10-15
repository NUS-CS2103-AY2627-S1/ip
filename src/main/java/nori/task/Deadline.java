package nori.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    private final LocalDate by;
    private final LocalTime time;

    /**
     * Creates a deadline with a date and time.
     *
     * @param description description shown to the user.
     * @param by deadline date.
     * @param time deadline time.
     */
    public Deadline(String description, LocalDate by, LocalTime time) {
        super(description);
        assert by != null : "Deadline date must not be null";
        assert time != null : "Deadline time must not be null";
        this.by = by;
        this.time = time;
    }

    /**
     * Returns the deadline date.
     *
     * @return deadline date
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns the deadline time.
     *
     * @return deadline time
     */
    public LocalTime getTime() {
        return time;
    }

    @Override
    public String toString() {
        String formattedDate = by.format(DISPLAY_FORMAT);
        String formattedDeadline = formattedDate + ", " + time.format(TIME_DISPLAY_FORMAT);
        return "[D]" + super.toString() + " (by: " + formattedDeadline + ")";
    }
}
