package bkxss;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * A task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter
            .ofPattern("MMM dd uuuu HH:mm", Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT);
    private final LocalDateTime by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description text that describes the task
     * @param by parsed deadline supplied by the user
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /** Returns the deadline text for persistence. */
    public LocalDateTime getBy() {
        return by;
    }

    /** Returns the deadline in the same format shown by the list command. */
    public String getFormattedBy() {
        return by.format(DISPLAY_FORMAT);
    }

    /** Parses the display format used when deadlines are saved. */
    public static LocalDateTime parseFormattedBy(String text) {
        return LocalDateTime.parse(text.trim(), DISPLAY_FORMAT);
    }

    /** Returns the display text for this deadline task. */
    @Override
    public String toString() {
        return "[D][" + getStatusIcon() + "] " + description + " (by: " + getFormattedBy() + ")";
    }
}
