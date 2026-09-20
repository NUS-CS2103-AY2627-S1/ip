package todd;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

/** Parses user-entered dates and formats them for Todd's responses. */
public final class DateTimeUtil {
    private static final DateTimeFormatter INPUT_DATE = DateTimeFormatter
            .ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_TIME = DateTimeFormatter
            .ofPattern("HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter
            .ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter
            .ofPattern("MMM dd uuuu, h:mma", Locale.ENGLISH);

    private DateTimeUtil() {
    }

    /**
     * Parses either a date or a date with a 24-hour time.
     * A date without a time is represented as midnight on that date.
     *
     * @param input date text in yyyy-MM-dd or yyyy-MM-dd HHmm format
     * @return the parsed date and time
     * @throws TodException if the input is not a real date in a supported format
     */
    public static LocalDateTime parseDateTime(String input) throws TodException {
        return parseDateTime(input, LocalDate.now());
    }

    /** Parses a date-time relative to a supplied date, allowing deterministic tests. */
    static LocalDateTime parseDateTime(String input, LocalDate today) throws TodException {
        String[] parts = input.trim().split("\\s+", 2);
        LocalDate date = parseDate(parts[0], today);
        try {
            LocalTime time = parts.length == 1 ? LocalTime.MIDNIGHT : LocalTime.parse(parts[1], INPUT_TIME);
            return date.atTime(time);
        } catch (DateTimeParseException e) {
            throw new TodException("That time doesn't look right, dawg. "
                    + "Use a 24-hour time from 0000 to 2359, like 1430 for 2:30pm.");
        }
    }

    /**
     * Parses the date used by commands that search for tasks on a specific day.
     *
     * @param input date text in yyyy-MM-dd format
     * @return the parsed date
     * @throws TodException if the input is not a real date in the required format
     */
    public static LocalDate parseDate(String input) throws TodException {
        return parseDate(input, LocalDate.now());
    }

    /** Parses a date relative to a supplied date, allowing deterministic tests. */
    static LocalDate parseDate(String input, LocalDate today) throws TodException {
        try {
            return parseDateToken(input.trim(), today);
        } catch (DateTimeParseException e) {
            throw new TodException(
                    "That date doesn't look right, dawg. Check that the day and month actually exist.\n"
                    + "Try yyyy-MM-dd (like 2026-09-17), today, tomorrow, or a weekday such as Mon.");
        }
    }

    /** Parses an absolute date or a supported date word relative to today. */
    private static LocalDate parseDateToken(String input, LocalDate today) {
        try {
            return LocalDate.parse(input, INPUT_DATE);
        } catch (DateTimeParseException e) {
            String normalizedInput = input.toLowerCase(Locale.ENGLISH);
            if (normalizedInput.equals("today")) {
                return today;
            }
            if (normalizedInput.equals("tomorrow")) {
                return today.plusDays(1);
            }

            for (DayOfWeek day : DayOfWeek.values()) {
                String shortName = day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toLowerCase(Locale.ENGLISH);
                String fullName = day.getDisplayName(TextStyle.FULL, Locale.ENGLISH).toLowerCase(Locale.ENGLISH);
                if (normalizedInput.equals(shortName) || normalizedInput.equals(fullName)) {
                    return today.with(TemporalAdjusters.next(day));
                }
            }
            throw e;
        }
    }

    /** Formats a stored date and omits the time when it is midnight. */
    public static String format(LocalDateTime dateTime) {
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(DISPLAY_DATE);
        }
        return dateTime.format(DISPLAY_DATE_TIME);
    }

    /** Formats a date without a time for a user-facing response. */
    public static String format(LocalDate date) {
        return date.format(DISPLAY_DATE);
    }
}
