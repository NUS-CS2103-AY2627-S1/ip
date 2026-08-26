import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a date, optionally with a time of day, used for deadlines and events.
 * Accepts either {@code yyyy-MM-dd HHmm} (a date with a 24-hour time, e.g.
 * {@code 2019-10-15 1800}) or {@code yyyy-MM-dd} (a date alone, e.g. {@code 2019-10-15}).
 * A date entered without a time is stored internally as midnight but remembers that no
 * time was given, so it prints and saves without one.
 */
public final class TaskDateTime {
    // Input formats accepted from the user and from the data file.
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Display format shown to the user, e.g. "Oct 15 2019, 6:00 pm".
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy, h:mm a");
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");

    private final LocalDateTime dateTime;
    private final boolean hasTime;

    private TaskDateTime(LocalDateTime dateTime, boolean hasTime) {
        this.dateTime = dateTime;
        this.hasTime = hasTime;
    }

    /**
     * Parses text as either {@code yyyy-MM-dd HHmm} or, failing that, {@code yyyy-MM-dd}.
     * This is used both for text typed by the user and for text previously written to the
     * data file by {@link #toSaveFormat()}, since both use the same two formats.
     *
     * @param text the text to parse.
     * @return the parsed date, with its time of day if one was given.
     * @throws DateTimeParseException if the text matches neither format.
     */
    public static TaskDateTime parse(String text) {
        try {
            return new TaskDateTime(LocalDateTime.parse(text, INPUT_DATE_TIME_FORMAT), true);
        } catch (DateTimeParseException exception) {
            // Not "yyyy-MM-dd HHmm"; fall back to a date alone. If this also fails, its
            // exception is the one that propagates to the caller.
            LocalDate date = LocalDate.parse(text, INPUT_DATE_FORMAT);
            return new TaskDateTime(date.atStartOfDay(), false);
        }
    }

    /**
     * Returns this date's representation for the data file: {@code yyyy-MM-dd HHmm} if a
     * time was given, or plain {@code yyyy-MM-dd} otherwise. Both are read back directly by
     * {@link #parse(String)}.
     *
     * @return the text to write to the data file.
     */
    public String toSaveFormat() {
        return hasTime ? dateTime.format(INPUT_DATE_TIME_FORMAT) : dateTime.toLocalDate().toString();
    }

    /**
     * Returns this date formatted for display, e.g. {@code Oct 15 2019, 6:00 pm} if a time
     * was given, or {@code Oct 15 2019} otherwise.
     *
     * @return the display text for this date.
     */
    @Override
    public String toString() {
        if (!hasTime) {
            return dateTime.format(DISPLAY_DATE_FORMAT);
        }

        // DateTimeFormatter's "a" pattern renders upper-case AM/PM; lower-casing it matches
        // the lower-case "am"/"pm" style used elsewhere in Mona's output.
        String formatted = dateTime.format(DISPLAY_DATE_TIME_FORMAT);
        return formatted.replace("AM", "am").replace("PM", "pm");
    }
}
