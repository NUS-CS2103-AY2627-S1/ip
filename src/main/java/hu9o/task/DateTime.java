package hu9o.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Parses and formats the date-times used by {@link DeadlineTask} and
 * {@link EventTask}.
 *
 * <p>
 * Input is accepted in {@code d/M/yy h a} form for a whole hour (for example
 * {@code 12/08/26 3 PM}) or {@code d/M/yy hmm a} form when minutes are given
 * (for example {@code 12/08/26 330 PM}). Dates are checked strictly, so a
 * nonexistent date such as {@code 31/2/26} is rejected rather than quietly moved
 * to the last day of the month. Two output forms are produced: a storage form
 * that round-trips through {@link #parseString(String)}, and a friendlier
 * display form.
 */
public class DateTime {
    /** Formatter for a whole hour, such as {@code 12/8/26 3 PM}. */
    private static final DateTimeFormatter HOUR_FORMAT = createInputFormat("d/M/uu h a");

    /** Formatter for an hour with minutes and no separator, such as {@code 12/8/26 330 PM}. */
    private static final DateTimeFormatter HOUR_MINUTE_FORMAT = createInputFormat("d/M/uu hmm a");

    private DateTime() {
    }

    /**
     * Creates a case-insensitive formatter that rejects impossible dates.
     * Two separate formatters are used because a single {@code h[mm]} pattern
     * cannot tell the hour from the minutes in text such as {@code 630}.
     *
     * @param pattern the {@link DateTimeFormatter} pattern to read.
     * @return the strict formatter.
     */
    private static DateTimeFormatter createInputFormat(String pattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Parses a date-time written as {@code d/M/yy h a} or {@code d/M/yy hmm a}.
     *
     * @param text the date-time text to parse.
     * @return the parsed date-time.
     * @throws DateTimeParseException if the text is not in either form, or is not a real date.
     */
    public static LocalDateTime parseString(String text) throws DateTimeParseException {
        try {
            return LocalDateTime.parse(text, HOUR_FORMAT);
        } catch (DateTimeParseException hourFormatFailure) {
            return LocalDateTime.parse(text, HOUR_MINUTE_FORMAT);
        }
    }

    /**
     * Formats a date-time for the save file, in a form that
     * {@link #parseString(String)} can read back.
     *
     * @param dateTime the date-time to format.
     * @return the storage-form text, such as {@code 12/08/26 3 PM}.
     */
    public static String formatForStorage(LocalDateTime dateTime) {
        // Only ever called on an already-parsed field of a Deadline/Event task.
        assert dateTime != null : "formatForStorage needs a date-time to format";
        String pattern = dateTime.getMinute() > 0 ? "dd/MM/yy hmm a" : "dd/MM/yy h a";
        return dateTime.format(DateTimeFormatter.ofPattern(pattern, Locale.US));
    }

    /**
     * Formats a date-time for display to the user. The year is always shown so
     * a deadline next year is not mistaken for one this year.
     *
     * @param dateTime the date-time to format.
     * @return the display-form text, such as {@code 12 Aug 2026, 3 PM}.
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        // Only ever called on an already-parsed field of a Deadline/Event task.
        assert dateTime != null : "formatForDisplay needs a date-time to format";
        String pattern = dateTime.getMinute() > 0 ? "dd MMM yyyy, h:mm a" : "dd MMM yyyy, h a";
        return dateTime.format(DateTimeFormatter.ofPattern(pattern, Locale.US));
    }
}
