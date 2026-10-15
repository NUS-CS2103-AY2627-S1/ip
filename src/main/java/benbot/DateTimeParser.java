package benbot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.Map;

/** Converts user-entered dates into the date-time values used by scheduled tasks. */
public final class DateTimeParser {
    /** Date format accepted without a time, for example {@code 1/12/2029}. */
    private static final DateTimeFormatter DATE = DateTimeFormatter
            .ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);

    /** Date-time format accepted by the chatbot, for example {@code 2/12/2019 1800}. */
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter
            .ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT);

    /** Format used when showing a date without a time. */
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);

    /** Format used when showing a date and time. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME = new DateTimeFormatterBuilder()
            .appendPattern("MMM dd uuuu h:mm ")
            .appendText(ChronoField.AMPM_OF_DAY, Map.of(0L, "am", 1L, "pm"))
            .toFormatter(Locale.ENGLISH);

    /** Prevents construction because this class only contains utility methods. */
    private DateTimeParser() {
    }

    /**
     * Parses a date or date-time. A date without a time is represented as the start of that day.
     *
     * @param text the text following a scheduling marker such as {@code /by}.
     * @return the parsed value and whether the user supplied a time.
     * @throws InvalidCommandException if the text is not a valid supported date or date-time.
     */
    public static ParsedDateTime parse(String text) throws InvalidCommandException {
        try {
            if (!text.contains(" ")) {
                return new ParsedDateTime(LocalDate.parse(text, DATE).atStartOfDay(), false);
            }
            return new ParsedDateTime(LocalDateTime.parse(text, DATE_TIME), true);
        } catch (DateTimeParseException e) {
            throw new InvalidCommandException(
                    "Use a date such as 1/12/2029, or a date and time such as 2/12/2019 1800.");
        }
    }

    /**
     * Returns a parsed date in BenBot's user-facing format.
     *
     * @param value the date and time to format.
     * @param includesTime whether the value includes a user-supplied time.
     * @return the formatted date, with a time when {@code includesTime} is {@code true}.
     */
    public static String format(LocalDateTime value, boolean includesTime) {
        return includesTime ? value.format(DISPLAY_DATE_TIME) : value.format(DISPLAY_DATE);
    }

    /**
     * Returns a parsed date in a stable format that can be loaded again later.
     *
     * @param value the date and time to format.
     * @param includesTime whether the value includes a user-supplied time.
     * @return the formatted date in the format accepted by {@link #parse(String)}.
     */
    public static String formatForStorage(LocalDateTime value, boolean includesTime) {
        return includesTime ? value.format(DateTimeFormatter.ofPattern("d/M/uuuu HHmm"))
                : value.toLocalDate().format(DATE);
    }

    /**
     * A parsed scheduling value together with whether the command included a time.
     *
     * @param value the parsed date and time.
     * @param includesTime whether the command included a time.
     */
    public record ParsedDateTime(LocalDateTime value, boolean includesTime) {
    }
}
