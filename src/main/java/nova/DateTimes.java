package nova;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map;

/**
 * Converts between the date-time text a user types, the text shown back to
 * them, and the text stored in the save file.
 *
 * <p>A task's time is always kept as a {@link LocalDateTime}. When the user
 * gives a date with no time of day, midnight is stored and the time is left
 * out when displaying, so "2019-12-02" reads back as "Dec 02 2019" rather
 * than "Dec 02 2019, 12:00 am".
 */
public final class DateTimes {

    /**
     * Input formats accepted for a date plus a time of day.
     *
     * <p>These are strict, so a date such as 2019-02-30 is rejected rather
     * than quietly shifted to the end of the month. Strict resolution needs
     * the {@code uuuu} year pattern rather than {@code yyyy}.
     */
    private static final List<DateTimeFormatter> DATE_TIME_FORMATS = List.of(
            createStrictFormatter("uuuu-MM-dd HHmm"),
            createStrictFormatter("uuuu-MM-dd HH:mm"),
            createStrictFormatter("d/M/uuuu HHmm"),
            createStrictFormatter("d/M/uuuu HH:mm"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    /** Input formats accepted for a bare date, which is read as midnight. */
    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            createStrictFormatter("uuuu-MM-dd"),
            createStrictFormatter("d/M/uuuu"));

    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy");
    /**
     * Display format for a date with a time of day. Java has no pattern letter
     * for a lowercase am/pm, so the marker is supplied as explicit text rather
     * than uppercased by the formatter and lowercased again afterwards.
     */
    private static final DateTimeFormatter DISPLAY_DATE_TIME = new DateTimeFormatterBuilder()
            .appendPattern("MMM dd yyyy, h:mm")
            .appendText(ChronoField.AMPM_OF_DAY, Map.of(0L, "am", 1L, "pm"))
            .toFormatter();

    /** Utility class: not meant to be instantiated. */
    private DateTimes() {
    }

    /** Builds a formatter that rejects dates that do not exist. */
    private static DateTimeFormatter createStrictFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Parses user input into a date and time.
     *
     * @param input text such as {@code 2019-12-02 1800} or {@code 2/12/2019}
     * @return the parsed date and time, at midnight if no time was given
     * @throws NovaException if the text matches none of the accepted formats
     */
    public static LocalDateTime parse(String input) throws NovaException {
        String trimmed = input.trim();

        for (DateTimeFormatter format : DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(trimmed, format);
            } catch (DateTimeParseException e) {
                // Try the next format.
            }
        }

        for (DateTimeFormatter format : DATE_FORMATS) {
            try {
                return LocalDate.parse(trimmed, format).atStartOfDay();
            } catch (DateTimeParseException e) {
                // Try the next format.
            }
        }

        throw new NovaException("\"" + trimmed + "\" didn't parse as a date. "
                + "Try 2019-12-02, or 2019-12-02 1800 for a time.");
    }

    /**
     * Parses user input into a plain date, ignoring any time of day that was
     * supplied.
     *
     * @param input text such as {@code 2019-12-02} or {@code 2/12/2019}
     * @return the parsed date
     * @throws NovaException if the text matches none of the accepted formats
     */
    public static LocalDate parseDate(String input) throws NovaException {
        return parse(input).toLocalDate();
    }

    /**
     * Formats a plain date for display.
     *
     * @param date the value to format
     * @return text such as {@code Dec 02 2019}
     */
    public static String formatDate(LocalDate date) {
        return date.format(DISPLAY_DATE);
    }

    /**
     * Formats a date and time for display, omitting the time when it is
     * midnight.
     *
     * @param dateTime the value to format
     * @return text such as {@code Dec 02 2019} or {@code Dec 02 2019, 6:00pm}
     */
    public static String format(LocalDateTime dateTime) {
        assert dateTime != null : "a task with no date should not reach the formatter";
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(DISPLAY_DATE);
        }
        return dateTime.format(DISPLAY_DATE_TIME);
    }

    /**
     * Formats a date and time for the save file, using ISO-8601 so that it
     * round-trips exactly.
     *
     * @param dateTime the value to format
     * @return text such as {@code 2019-12-02T18:00}
     */
    public static String toFileString(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
