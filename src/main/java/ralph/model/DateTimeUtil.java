package ralph.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Utilities for consistent date/time formatting across the application.
 */
public final class DateTimeUtil {
    private static final DateTimeFormatter DATE_FMT =
        DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_FMT =
        DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    private DateTimeUtil() { }

    /**
     * Formats a LocalDateTime as either a date (if time is midnight) or date + time.
     */
    public static String formatDateOrDateTime(LocalDateTime dt) {
        if (dt.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return DATE_FMT.format(dt.toLocalDate());
        } else {
            return DATE_FMT.format(dt.toLocalDate()) + " " + TIME_FMT.format(dt.toLocalTime());
        }
    }

    /**
     * Formats only the date part of the provided LocalDateTime using the project''s standard formatter.
     *
     * @param dt LocalDateTime whose date part is to be formatted
     * @return formatted date string in the form "MMM dd yyyy" (Locale.ENGLISH)
     */
    public static String formatDate(LocalDateTime dt) {
        return DATE_FMT.format(dt.toLocalDate());
    }

    /**
     * Formats only the time part of the provided LocalDateTime using the project''s standard formatter.
     *
     * @param dt LocalDateTime whose time part is to be formatted
     * @return formatted time string in the form "HH:mm" (Locale.ENGLISH)
     */
    public static String formatTime(LocalDateTime dt) {
        return TIME_FMT.format(dt.toLocalTime());
    }
}
