package braun.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import braun.exception.BraunException;

/**
 * Utility class for parsing, validating, and formatting dates and times across Braun.
 * Supports multiple standard formats including ISO ({@code yyyy-MM-dd}), slash formats ({@code d/M/yyyy}),
 * and 24-hour time notations ({@code HHmm}).
 */
public class DateTimeUtil {

    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT =
            DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH);

    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("d/M/yyyy HHmm", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("d-M-yyyy HHmm", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("yyyy/MM/dd HHmm", Locale.ENGLISH)
    };

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("d-M-yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.ENGLISH)
    };

    private static final DateTimeFormatter[] TIME_FORMATTERS = {
        DateTimeFormatter.ofPattern("HHmm", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)
    };

    /**
     * Prevents instantiation of this utility class.
     */
    private DateTimeUtil() {
    }

    /**
     * Encapsulates a date with an optional time component.
     */
    public static class TaskDateTime {
        private final LocalDate date;
        private final LocalTime time;

        /**
         * Constructs a new {@code TaskDateTime} with the specified date and optional time.
         *
         * @param date the date component.
         * @param time the time component, or {@code null} if only a date was specified.
         */
        public TaskDateTime(LocalDate date, LocalTime time) {
            assert date != null : "Date component of TaskDateTime must not be null.";
            this.date = date;
            this.time = time;
        }

        /**
         * Returns the date component of this date/time.
         *
         * @return the local date.
         */
        public LocalDate getDate() {
            return date;
        }

        /**
         * Returns the time component of this date/time.
         *
         * @return the local time, or {@code null} if no time is set.
         */
        public LocalTime getTime() {
            return time;
        }

        /**
         * Checks whether this instance includes a time component.
         *
         * @return {@code true} if a time component is present, {@code false} otherwise.
         */
        public boolean hasTime() {
            return time != null;
        }

        /**
         * Formats the date/time for user display (e.g. "Aug 30 2026, 5:00PM" or "Aug 30 2026").
         *
         * @return display string.
         */
        public String formatForDisplay() {
            if (time != null) {
                return date.format(DISPLAY_DATE_FORMAT) + ", " + time.format(DISPLAY_TIME_FORMAT);
            }
            return date.format(DISPLAY_DATE_FORMAT);
        }

        /**
         * Formats the date/time for storage file persistence.
         *
         * @return storage string.
         */
        public String formatForFile() {
            if (time != null) {
                return date.toString() + " " + time.format(DateTimeFormatter.ofPattern("HHmm", Locale.ENGLISH));
            }
            return date.toString();
        }

        /**
         * Checks if this date/time occurs before another {@code TaskDateTime}.
         *
         * @param other the other date/time to compare against.
         * @return {@code true} if this date/time is chronologically before {@code other}, {@code false} otherwise.
         */
        public boolean isBefore(TaskDateTime other) {
            if (!this.date.equals(other.date)) {
                return this.date.isBefore(other.date);
            }
            if (this.time != null && other.time != null) {
                return this.time.isBefore(other.time);
            }
            return false;
        }

        /**
         * Returns the string representation of this date/time formatted for display.
         *
         * @return display formatted date/time string.
         */
        @Override
        public String toString() {
            return formatForDisplay();
        }
    }

    /**
     * Parses a date or date-time text string into a {@link TaskDateTime}.
     *
     * @param text the date/time string to parse.
     * @return parsed {@link TaskDateTime}.
     * @throws BraunException if the text cannot be parsed with any supported format.
     */
    public static TaskDateTime parse(String text) throws BraunException {
        return parse(text, null);
    }

    /**
     * Parses a date or date-time text string into a {@link TaskDateTime}, with optional default date fallback
     * when only a time is provided.
     *
     * @param text the date/time string to parse.
     * @param defaultDate fallback date to use if only time is given, or null.
     * @return parsed {@link TaskDateTime}.
     * @throws BraunException if the text cannot be parsed.
     */
    public static TaskDateTime parse(String text, LocalDate defaultDate) throws BraunException {
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            throw new BraunException("*static* Date and time cannot be empty.");
        }

        // 1. Try date + time formats
        for (DateTimeFormatter dtf : DATE_TIME_FORMATTERS) {
            try {
                LocalDateTime ldt = LocalDateTime.parse(trimmed, dtf);
                return new TaskDateTime(ldt.toLocalDate(), ldt.toLocalTime());
            } catch (DateTimeParseException ignored) {
                // Try next
            }
        }

        // 2. Try date-only formats
        for (DateTimeFormatter df : DATE_FORMATTERS) {
            try {
                LocalDate ld = LocalDate.parse(trimmed, df);
                return new TaskDateTime(ld, null);
            } catch (DateTimeParseException ignored) {
                // Try next
            }
        }

        // 3. If defaultDate provided, try time-only formats
        if (defaultDate != null) {
            for (DateTimeFormatter tf : TIME_FORMATTERS) {
                try {
                    LocalTime lt = LocalTime.parse(trimmed, tf);
                    return new TaskDateTime(defaultDate, lt);
                } catch (DateTimeParseException ignored) {
                    // Try next
                }
            }
        }

        throw new BraunException("*static* Invalid date format! Please use yyyy-MM-dd (e.g. 2026-08-30) "
                + "or yyyy-MM-dd HHmm / d/M/yyyy HHmm (e.g. 2026-08-30 1700 or 2/12/2019 1800).");
    }

    /**
     * Parses a date-only search query string into a {@link LocalDate}.
     *
     * @param text search date string.
     * @return parsed {@link LocalDate}.
     * @throws BraunException if invalid format.
     */
    public static LocalDate parseDate(String text) throws BraunException {
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            throw new BraunException("*static* Please specify a date to search for (e.g. date 2026-08-30).");
        }

        for (DateTimeFormatter df : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(trimmed, df);
            } catch (DateTimeParseException ignored) {
                // Try next
            }
        }

        throw new BraunException("*static* Invalid date format! Please use yyyy-MM-dd or d/M/yyyy (e.g. 2026-08-30).");
    }

    /**
     * Formats a {@link LocalDate} using standard display format (MMM dd yyyy).
     *
     * @param date date to format.
     * @return formatted date string.
     */
    public static String formatDate(LocalDate date) {
        assert date != null : "Date to format cannot be null.";
        return date.format(DISPLAY_DATE_FORMAT);
    }
}
