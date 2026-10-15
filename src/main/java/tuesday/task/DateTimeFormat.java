package tuesday.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Shares the date-time format used to display, save, and reload scheduled tasks.
 */
public final class DateTimeFormat {
    /** Formats dates in the task display and save format. */
    public static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    /** Formats times in the task display and save format. */
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private DateTimeFormat() {
    }

    /**
     * Formats the available components, separating a date and time with one space.
     *
     * @param date The optional date, or null when absent.
     * @param time The optional time, or null when absent.
     * @return The formatted components, or an empty string when both are absent.
     */
    public static String format(LocalDate date, LocalTime time) {
        String result = date == null ? "" : date.format(DATE_FORMATTER);
        if (time != null) {
            if (!result.isEmpty()) {
                result += " ";
            }
            result += time.format(TIME_FORMATTER);
        }
        return result;
    }
}
