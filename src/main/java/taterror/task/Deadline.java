package taterror.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * A task that needs to be done by a certain date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final String byRaw;

    /**
     * Creates a deadline with the given description and raw "by" date text.
     */
    public Deadline(String description, String byRaw) {
        super(description);
        this.byRaw = byRaw;
    }

    /**
     * Returns the raw "by" text exactly as the user typed it (before any date
     * parsing), used both for the save file and as a display fallback if it
     * isn't a valid ISO-8601 date.
     */
    public String getByRaw() {
        return byRaw;
    }

    @Override
    public String getTypeCode() {
        return "D";
    }

    @Override
    public String toSaveDetail() {
        return " | " + byRaw;
    }

    @Override
    public String toDisplayDetail() {
        return "(by: " + formatBy() + ")";
    }

    /**
     * Formats {@link #byRaw} for display. If it parses as an ISO-8601 date
     * (e.g. {@code 2019-10-15}), renders as "MMM dd yyyy" (e.g. "Oct 15 2019");
     * otherwise returns it unchanged, so a freeform date string doesn't crash
     * the display.
     */
    private String formatBy() {
        try {
            LocalDate date = LocalDate.parse(byRaw.trim());
            return date.format(DISPLAY_FORMAT);
        } catch (DateTimeParseException e) {
            return byRaw;
        }
    }
}
