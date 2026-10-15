package slotbot.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed before a specified time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private final LocalDate date;

    /**
     * Creates a deadline.
     *
     * @param description Deadline description.
     * @param date Deadline date.
     */
    public Deadline(String description, LocalDate date) {
        super(description);
        this.date = date;
    }

    /**
     * Returns this deadline's date.
     *
     * @return Deadline date.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the deadline's display text.
     *
     * @return Deadline type, status, description, and date.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + date.format(DISPLAY_FORMATTER) + ")";
    }
}
