package odysseus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A task that must be completed by a given date or time string.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private final LocalDate by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description text describing the task.
     * @param by calendar date by which the task must be completed.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    @Override
    protected String getTypeMarker() {
        return "D";
    }

    @Override
    protected String getDetails() {
        return " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns the saved deadline text.
     */
    protected LocalDate getBy() {
        return by;
    }
}
