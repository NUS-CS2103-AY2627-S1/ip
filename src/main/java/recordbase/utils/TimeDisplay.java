package recordbase.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Formats stored date-time values and durations for user-facing task output.
 *
 * <p>This utility keeps presentation rules separate from task models so all task types use
 * consistent date, duration, and deadline wording.</p>
 */
public final class TimeDisplay {
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d MMM uuuu, h:mm a");

    /**
     * Prevents construction of this stateless utility class.
     */
    private TimeDisplay() { }

    /**
     * Formats a date and time without exposing its storage-oriented ISO representation.
     *
     * @param dateTime value to format
     * @return date and time in Record's user-facing format
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMAT);
    }

    /**
     * Describes the calendar duration between two date-time values.
     *
     * @param start beginning of the interval
     * @param end end of the interval
     * @return human-readable duration containing only non-zero components
     */
    public static String formatDuration(LocalDateTime start, LocalDateTime end) {
        Period period = Period.between(start.toLocalDate(), end.toLocalDate());
        LocalDateTime anchor = start.plus(period);
        if (anchor.isAfter(end)) {
            period = period.minusDays(1);
            anchor = start.plus(period);
        }
        Duration remainder = Duration.between(anchor, end);
        return joinParts(period.getYears(), period.getMonths(), period.getDays(),
                remainder.toHours(), remainder.toMinutesPart());
    }

    /**
     * Describes how long remains before a deadline or how long it has been overdue.
     *
     * @param deadline deadline to compare with the current system time
     * @return concise deadline status suitable for display beside a task
     */
    public static String formatDeadlineStatus(LocalDateTime deadline) {
        LocalDateTime now = LocalDateTime.now();
        boolean overdue = deadline.isBefore(now);
        String duration = overdue ? formatDuration(deadline, now) : formatDuration(now, deadline);
        return overdue ? "overdue by " + duration : "due in " + duration;
    }

    /**
     * Joins non-zero duration components into a readable phrase.
     *
     * @param years number of complete calendar years
     * @param months number of remaining calendar months
     * @param days number of remaining calendar days
     * @param hours number of remaining hours
     * @param minutes number of remaining minutes
     * @return formatted duration, or {@code less than a minute} when all parts are zero
     */
    private static String joinParts(int years, int months, int days, long hours, int minutes) {
        List<String> parts = new ArrayList<>();
        addPart(parts, years, "year");
        addPart(parts, months, "month");
        addPart(parts, days, "day");
        addPart(parts, hours, "hour");
        addPart(parts, minutes, "minute");
        return parts.isEmpty() ? "less than a minute" : String.join(" ", parts);
    }

    /**
     * Adds one non-zero, correctly pluralised duration component to a result list.
     *
     * @param parts destination for the formatted component
     * @param value numeric component value
     * @param unit singular name of the component's unit
     */
    private static void addPart(List<String> parts, long value, String unit) {
        if (value != 0) {
            parts.add(value + " " + unit + (value == 1 ? "" : "s"));
        }
    }
}
