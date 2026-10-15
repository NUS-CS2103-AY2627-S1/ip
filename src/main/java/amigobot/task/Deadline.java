package amigobot.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that needs to be done before a specific date.
 * The date can be a parsed LocalDate or a plain string (e.g. "tomorrow").
 */
public class Deadline extends Task {

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");

    protected LocalDate byDate;
    protected String byString;

    /**
     * Creates a Deadline with a parsed date.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.byDate = by;
        this.byString = null;
    }

    /**
     * Creates a Deadline with a plain string date (e.g. "tomorrow", "next week").
     */
    public Deadline(String description, String by) {
        super(description);
        this.byDate = null;
        this.byString = by;
    }

    /**
     * Returns the deadline date as a LocalDate, or null if stored as a string.
     *
     * @return the deadline date, or null.
     */
    public LocalDate getByDate() {
        return byDate;
    }

    /**
     * Returns the deadline date as a plain string, or null if stored as a LocalDate.
     *
     * @return the deadline string, or null.
     */
    public String getByString() {
        return byString;
    }

    /**
     * Returns a string representation prefixed with [D] and the deadline,
     * e.g. "[D][ ] return book (by: Dec 2 2025)".
     *
     * @return the formatted deadline string.
     */
    @Override
    public String toString() {
        String display = byDate != null ? byDate.format(DISPLAY_FORMAT) : byString;
        return "[D]" + super.toString() + " (by: " + display + ")";
    }
}
