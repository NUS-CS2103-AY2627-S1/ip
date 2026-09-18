package alfred.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.Locale;

/**
 * A calendar date, optionally with a time of day, used by deadlines and events.
 */
public class TaskDateTime {
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma", Locale.ENGLISH);

    private static final DateTimeFormatter[] USER_DATE_TIMES = formatters(
            "uuuu-MM-dd HHmm",
            "uuuu-MM-dd HH:mm",
            "d/M/uuuu HHmm",
            "d/M/uuuu H:mm");
    private static final DateTimeFormatter[] USER_DATES = formatters(
            "uuuu-MM-dd",
            "d/M/uuuu");

    private final LocalDateTime dateTime;
    private final boolean hasTime;

    private TaskDateTime(LocalDateTime dateTime, boolean hasTime) {
        // Every factory method constructs this only after a successful parse.
        assert dateTime != null : "TaskDateTime always wraps a concrete date-time";
        this.dateTime = dateTime;
        this.hasTime = hasTime;
    }

    /**
     * Returns a date-time parsed from user input, or {@code null} if unrecognized.
     *
     * <p>Accepted date forms are {@code yyyy-MM-dd} and {@code d/M/yyyy}. A time of
     * {@code HHmm} or {@code HH:mm} may follow the date.
     *
     * @param text Raw date or date-time typed by the user.
     * @return Parsed value, or {@code null} if {@code text} is not an accepted form.
     */
    public static TaskDateTime parseUserInput(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String trimmed = text.trim();
        TaskDateTime withTime = parseWithFormatters(trimmed, USER_DATE_TIMES, true);
        if (withTime != null) {
            return withTime;
        }
        return parseWithFormatters(trimmed, USER_DATES, false);
    }

    /**
     * Returns a date-time parsed from the save file, or {@code null} if invalid.
     *
     * @param text ISO-8601 date or date-time from the save file.
     * @return Parsed value, or {@code null} if {@code text} is not ISO-8601.
     */
    public static TaskDateTime parseSaved(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String trimmed = text.trim();
        TaskDateTime dateTime = parseIsoDateTime(trimmed);
        if (dateTime != null) {
            return dateTime;
        }
        return parseIsoDate(trimmed);
    }

    /** Returns this value formatted for chatbot replies. */
    public String toDisplayString() {
        if (hasTime) {
            return dateTime.format(DISPLAY_DATE_TIME);
        }
        return dateTime.toLocalDate().format(DISPLAY_DATE);
    }

    /** Returns the calendar date formatted as {@code MMM dd yyyy}. */
    public String toDisplayDate() {
        return dateTime.toLocalDate().format(DISPLAY_DATE);
    }

    /** Returns this value as ISO-8601 for the save file. */
    public String toSaveString() {
        if (hasTime) {
            return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        return dateTime.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /** Returns the calendar date, ignoring any time of day. */
    public LocalDate toLocalDate() {
        return dateTime.toLocalDate();
    }

    /**
     * Returns {@code true} if this date-time is strictly after {@code other}.
     *
     * @param other Date-time to compare against.
     * @return {@code true} if this instant is later than {@code other}.
     */
    public boolean isAfter(TaskDateTime other) {
        // Event parsing compares two successfully parsed date-times.
        assert other != null : "Comparison requires another TaskDateTime";
        return dateTime.isAfter(other.dateTime);
    }

    private static TaskDateTime parseIsoDateTime(String text) {
        try {
            return new TaskDateTime(LocalDateTime.parse(text), true);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    private static TaskDateTime parseIsoDate(String text) {
        try {
            LocalDate date = LocalDate.parse(text);
            return new TaskDateTime(date.atStartOfDay(), false);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    private static TaskDateTime parseWithFormatters(String text, DateTimeFormatter[] formatters,
            boolean hasTime) {
        for (int i = 0; i < formatters.length; i++) {
            try {
                if (hasTime) {
                    LocalDateTime parsed = LocalDateTime.parse(text, formatters[i]);
                    return new TaskDateTime(parsed, true);
                }
                LocalDate date = LocalDate.parse(text, formatters[i]);
                return new TaskDateTime(date.atStartOfDay(), false);
            } catch (DateTimeParseException exception) {
                // Try the next accepted pattern.
            }
        }
        return null;
    }

    private static DateTimeFormatter[] formatters(String... patterns) {
        return Arrays.stream(patterns)
                .map(pattern -> DateTimeFormatter.ofPattern(pattern)
                        .withChronology(IsoChronology.INSTANCE)
                        .withResolverStyle(ResolverStyle.STRICT)
                        .withLocale(Locale.ENGLISH))
                .toArray(DateTimeFormatter[]::new);
    }
}
