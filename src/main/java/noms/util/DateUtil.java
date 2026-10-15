package noms.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.Map;

import noms.exception.InvalidDateException;

/**
 * Parses and formats the dates Noms accepts in deadline and event commands.
 *
 * Input is the ISO format {@code yyyy-MM-dd} (e.g. {@code 2019-10-15}) so
 * that {@link LocalDate#parse(CharSequence)} can be used directly. Output
 * for display uses a friendlier pattern (e.g. {@code Oct 15 2019}).
 */
public final class DateUtil {
    /**
     * Fixed three-letter month abbreviations, keyed by {@link ChronoField#MONTH_OF_YEAR}
     * value. The JDK's built-in {@code MMM} pattern instead derives the abbreviation from
     * locale (CLDR) data, whose short form for September has changed between JDK builds
     * ("Sep" vs "Sept"); spelling the months out here keeps {@link #format(LocalDate)}
     * deterministic across every environment the project runs or is graded on.
     */
    private static final Map<Long, String> MONTH_ABBREVIATIONS = Map.ofEntries(
            Map.entry(1L, "Jan"), Map.entry(2L, "Feb"), Map.entry(3L, "Mar"),
            Map.entry(4L, "Apr"), Map.entry(5L, "May"), Map.entry(6L, "Jun"),
            Map.entry(7L, "Jul"), Map.entry(8L, "Aug"), Map.entry(9L, "Sep"),
            Map.entry(10L, "Oct"), Map.entry(11L, "Nov"), Map.entry(12L, "Dec"));

    private static final DateTimeFormatter DISPLAY_FORMAT = new DateTimeFormatterBuilder()
            .appendText(ChronoField.MONTH_OF_YEAR, MONTH_ABBREVIATIONS)
            .appendLiteral(' ')
            .appendPattern("dd yyyy")
            .toFormatter(Locale.ROOT);

    private DateUtil() {} // prevents instantiation

    /**
     * Parses user input as an ISO-format date ({@code yyyy-MM-dd}).
     *
     * @param raw the date text typed by the user
     * @return the parsed date
     * @throws InvalidDateException if the text is not a valid ISO date
     */
    public static LocalDate parse(String raw) throws InvalidDateException {
        try {
            return LocalDate.parse(raw);
        } catch (DateTimeParseException e) {
            throw new InvalidDateException(raw);
        }
    }

    /**
     * Formats a date for display in task listings.
     *
     * @param date the date to format
     * @return the date rendered as e.g. {@code Oct 15 2019}, using the fixed
     *     three-letter month abbreviations in {@link #MONTH_ABBREVIATIONS}
     */
    public static String format(LocalDate date) {
        return date.format(DISPLAY_FORMAT);
    }
}
