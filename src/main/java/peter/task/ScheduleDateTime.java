package peter.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Parses, stores, and displays dates and times used by scheduled tasks.
 */
public final class ScheduleDateTime {
    private static final DateTimeFormatter USER_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy, h:mm a", Locale.ENGLISH);

    private ScheduleDateTime() {
    }

    /**
     * Parses either an ISO date or a date and time such as
     * {@code 2/12/2019 1800}. Date-only values start at midnight.
     *
     * @param value user-entered schedule value.
     * @return parsed date and time.
     * @throws DateTimeParseException if neither supported format matches.
     */
    public static LocalDateTime parseUserInput(String value) {
        try {
            return LocalDate.parse(value).atStartOfDay();
        } catch (DateTimeParseException e) {
            return LocalDateTime.parse(value, USER_DATE_TIME_FORMAT);
        }
    }

    /**
     * Parses an ISO date-time from storage, while remaining compatible with
     * date-only records created by earlier versions.
     *
     * @param value stored schedule value.
     * @return parsed date and time.
     * @throws DateTimeParseException if the stored value is invalid.
     */
    public static LocalDateTime parseStoredValue(String value) {
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            return LocalDate.parse(value).atStartOfDay();
        }
    }

    /**
     * Displays midnight values as dates and other values as dates with times.
     *
     * @param value date and time to display.
     * @return a friendly schedule value.
     */
    public static String format(LocalDateTime value) {
        if (value.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return value.format(DISPLAY_DATE_FORMAT);
        }
        return value.format(DISPLAY_DATE_TIME_FORMAT);
    }

    /**
     * Formats a date without adding a time.
     *
     * @param value date to display.
     * @return a friendly date such as {@code Oct 15 2019}.
     */
    public static String format(LocalDate value) {
        return value.format(DISPLAY_DATE_FORMAT);
    }
}
