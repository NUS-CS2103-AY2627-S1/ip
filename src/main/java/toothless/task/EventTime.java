package toothless.task;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import toothless.exception.ToothlessException;

/**
 * Validates the supported human-readable event times and their ordering.
 */
public final class EventTime {
    private static final Pattern TIME = Pattern.compile("(?i)(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)");
    private static final Pattern CLOCK = Pattern.compile("(\\d{1,2}):(\\d{2})");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);

    private EventTime() {
    }

    /**
     * Ensures both endpoints are real, comparable, and strictly increasing.
     * A time-only endpoint shares the other endpoint's date when one is given.
     *
     * @param from event start text.
     * @param to event end text.
     * @throws ToothlessException if either endpoint is invalid or their order is ambiguous.
     */
    public static void validate(String from, String to) throws ToothlessException {
        EventTimeValue start = parse(from);
        EventTimeValue end = parse(to);
        boolean hasIncompatibleDates = (start.day != null && end.day != null
                && !start.day.equals(end.day))
                || (start.date != null && end.day != null)
                || (end.date != null && start.day != null);
        if (hasIncompatibleDates) {
            throw new ToothlessException("Toothless needs comparable event dates.\n"
                    + "Use the same weekday or full dates for both endpoints.");
        }
        boolean isIncreasing = start.date != null && end.date != null
                ? start.date.atTime(start.time).isBefore(end.date.atTime(end.time))
                : start.time.isBefore(end.time);
        if (!isIncreasing) {
            throw new ToothlessException("The event's start must be before its end.\n"
                    + "Try: event meeting /from 2pm /to 3pm");
        }
    }

    /**
     * Parses a complete endpoint without guessing a date from the current clock.
     */
    private static EventTimeValue parse(String value) throws ToothlessException {
        String normalized = value.strip().replaceAll("\\s+", " ");
        if (normalized.length() > 100) {
            throw invalidTime();
        }
        if (normalized.equalsIgnoreCase("tomorrow")) {
            return new EventTimeValue(null, "tomorrow", LocalTime.MIDNIGHT);
        }
        Matcher timeMatcher = TIME.matcher(normalized);
        Matcher clockMatcher = CLOCK.matcher(normalized);
        int timeStart;
        LocalTime time;
        if (timeMatcher.find() && timeMatcher.end() == normalized.length()) {
            int hour = Integer.parseInt(timeMatcher.group(1));
            int minute = timeMatcher.group(2) == null ? 0 : Integer.parseInt(timeMatcher.group(2));
            if (hour < 1 || hour > 12 || minute > 59) {
                throw invalidTime();
            }
            time = LocalTime.of(hour % 12 + (timeMatcher.group(3).equalsIgnoreCase("pm") ? 12 : 0),
                    minute);
            timeStart = timeMatcher.start();
        } else if (clockMatcher.find() && clockMatcher.end() == normalized.length()) {
            int hour = Integer.parseInt(clockMatcher.group(1));
            int minute = Integer.parseInt(clockMatcher.group(2));
            if (hour > 23 || minute > 59) {
                throw invalidTime();
            }
            time = LocalTime.of(hour, minute);
            timeStart = clockMatcher.start();
        } else {
            throw invalidTime();
        }
        String dayText = normalized.substring(0, timeStart).strip();
        if (dayText.isEmpty()) {
            return new EventTimeValue(null, null, time);
        }
        if (dayText.equalsIgnoreCase("tomorrow") || dayText.equalsIgnoreCase("today")) {
            return new EventTimeValue(null, dayText.toLowerCase(Locale.ROOT), time);
        }
        try {
            DayOfWeek weekday = DayOfWeek.valueOf(dayText.toUpperCase(Locale.ROOT));
            return new EventTimeValue(null, weekday.name(), time);
        } catch (IllegalArgumentException ignored) {
            // The prefix may instead be a full calendar date.
        }
        try {
            LocalDate date = dayText.matches("\\d{4}-\\d{2}-\\d{2}")
                    ? DeadlineDate.parse(dayText) : LocalDate.parse(dayText, DATE);
            return new EventTimeValue(date, null, time);
        } catch (DateTimeParseException exception) {
            throw invalidTime();
        }
    }

    /**
     * Returns focused guidance for malformed event date and time values.
     */
    private static ToothlessException invalidTime() {
        return new ToothlessException("That event time made Toothless tilt his head.\n"
                + "Use 2pm, 14:30, or 21 September 2026 2pm with a real date and time.");
    }

    /**
     * Keeps a calendar date, a named day, or neither, alongside the clock time.
     */
    private record EventTimeValue(LocalDate date, String day, LocalTime time) {
    }
}
