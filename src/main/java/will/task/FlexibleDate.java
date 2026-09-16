package will.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * A deadline/event date-or-time value that understands the yyyy-MM-dd
 * format (e.g. "2019-10-15") as an actual java.time.LocalDate, while
 * still accepting free text (e.g. "Sunday", "no idea :-p") that doesn't
 * match that format, storing it as-is instead of rejecting it.
 *
 * A value recognized as a date is displayed as "MMM dd yyyy" (e.g.
 * "Oct 15 2019") instead of being echoed back verbatim.
 */
public class FlexibleDate {
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    // Exactly one of these is non-null.
    private final LocalDate date;
    private final String text;

    /**
     * Creates a FlexibleDate by interpreting the given raw text.
     *
     * @param value The raw text to interpret, e.g. "2019-10-15" or "Sunday".
     */
    public FlexibleDate(String value) {
        assert value != null : "FlexibleDate should never be constructed with a null value";
        LocalDate parsed;
        try {
            parsed = LocalDate.parse(value, INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            parsed = null;
        }
        this.date = parsed;
        this.text = (parsed == null) ? value : null;
        assert (this.date != null) != (this.text != null)
                : "Exactly one of date/text should be set, never both or neither";
    }

    /** How this value should be shown to the user, e.g. in toString(). */
    public String toDisplayString() {
        return (date != null) ? date.format(DISPLAY_FORMAT) : text;
    }

    /**
     * How this value should be written to the save file. A recognized
     * date is normalized back to yyyy-MM-dd so it round-trips through
     * save/load as the same LocalDate rather than as display text.
     */
    public String toSaveString() {
        return (date != null) ? date.format(INPUT_FORMAT) : text;
    }

    /**
     * The underlying date, or null if this value was free text that
     * didn't match yyyy-MM-dd. Lets callers (e.g. an "on &lt;date&gt;" query)
     * compare against it directly.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Parses a yyyy-MM-dd string the same way the constructor does,
     * for use where a date is required rather than optional (e.g. the
     * "on &lt;date&gt;" query command). Throws DateTimeParseException if it
     * doesn't match, unlike the constructor, which treats a mismatch as
     * "fall back to free text" instead of an error.
     */
    public static LocalDate parseExact(String value) {
        return LocalDate.parse(value, INPUT_FORMAT);
    }

    /** Formats an arbitrary LocalDate the same way toDisplayString() would. */
    public static String formatForDisplay(LocalDate date) {
        return date.format(DISPLAY_FORMAT);
    }
}
