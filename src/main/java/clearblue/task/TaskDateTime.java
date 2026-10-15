package clearblue.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Represents a deadline/event date-or-time value that may be a real
 * calendar date or arbitrary free text, depending on what the user typed.
 *
 * <p>Text matching the {@code yyyy-MM-dd} format (e.g. {@code 2019-06-06})
 * is parsed into a {@link LocalDate} and displayed as {@code MMM dd yyyy}
 * (e.g. {@code Jun 06 2019}). Any other text is kept and displayed exactly
 * as typed.
 */
public class TaskDateTime {
    // STRICT is required here: the default SMART resolver silently rolls an
    // invalid calendar date (e.g. "2019-04-31", which April doesn't have)
    // forward to the next valid date instead of rejecting it, which would
    // silently reinterpret a user's mistyped date. The pattern uses "uuuu"
    // (proleptic year) rather than "yyyy" (year-of-era) because STRICT
    // resolution of "yyyy" needs an explicit era to resolve a year, which
    // plain digits like "2019" never provide.
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final String rawText;
    private final LocalDate date;

    /**
     * Creates a date-or-text value from user input, parsing it as a date
     * when possible.
     *
     * @param text raw text entered by the user (e.g. after {@code /by})
     */
    public TaskDateTime(String text) {
        this.rawText = text;
        this.date = parseOrNull(text);
    }

    /**
     * Returns this value formatted for display: a real date is shown as
     * {@code MMM dd yyyy}; unparsed text is shown exactly as entered.
     *
     * @return display text
     */
    public String toDisplayString() {
        return date != null ? date.format(DISPLAY_FORMAT) : rawText;
    }

    /**
     * Returns this value as originally entered, for saving to disk. Saving
     * the raw text (rather than the display text) lets {@link Storage}
     * re-parse it into a date again on the next load.
     *
     * @return original raw text
     */
    public String toStorageString() {
        return rawText;
    }

    /**
     * Returns whether this value was recognized as a real calendar date.
     *
     * @return {@code true} if the original text parsed as {@code yyyy-MM-dd}
     */
    public boolean isDate() {
        return date != null;
    }

    /**
     * Returns the parsed date.
     *
     * @return the underlying {@link LocalDate}
     * @throws IllegalStateException if this value did not parse as a date
     */
    public LocalDate getDate() {
        if (date == null) {
            throw new IllegalStateException("\"" + rawText + "\" is not a recognized date");
        }
        return date;
    }

    /**
     * Returns this value formatted for display; same as {@link #toDisplayString()}.
     *
     * @return display text
     */
    @Override
    public String toString() {
        return toDisplayString();
    }

    /**
     * Parses text as a {@code yyyy-MM-dd} date, using the same format
     * accepted for {@code /by}, {@code /from}, and {@code /to} values.
     *
     * @param text text to parse
     * @return the parsed date, or {@code null} if it does not match the format
     */
    public static LocalDate parseDate(String text) {
        return parseOrNull(text);
    }

    /**
     * Formats a date the same way a recognized {@code /by}, {@code /from},
     * or {@code /to} value is displayed.
     *
     * @param date date to format
     * @return formatted text, e.g. {@code Jun 06 2019}
     */
    public static String formatDate(LocalDate date) {
        return date.format(DISPLAY_FORMAT);
    }

    /**
     * Parses text as {@link #INPUT_FORMAT}, swallowing a parse failure into
     * {@code null} rather than propagating it, since unparseable text is
     * valid free-form input here rather than an error.
     *
     * @param text text to parse
     * @return the parsed date, or {@code null} if it does not match the format
     */
    private static LocalDate parseOrNull(String text) {
        try {
            return LocalDate.parse(text, INPUT_FORMAT);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }
}
