package thomas.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import thomas.ThomasException;

/**
 * Utility methods for parsing and formatting date/time values used by tasks.
 */
public class DateTimeUtil {
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter SLASH_DATE = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final DateTimeFormatter LEGACY_DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_COMPACT = DateTimeFormatter.ofPattern("HHmm");
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma", Locale.ENGLISH);

    /**
     * Represents a parsed date with an optional time component.
     */
    public static class ParsedDateTime {
        private final LocalDate date;
        private final LocalTime time;

        /**
         * Creates a parsed date/time value.
         *
         * @param date Parsed date.
         * @param time Parsed time, or null if none was given.
         */
        public ParsedDateTime(LocalDate date, LocalTime time) {
            this.date = date;
            this.time = time;
        }

        /**
         * Returns the parsed date.
         *
         * @return calendar date
         */
        public LocalDate getDate() {
            return date;
        }

        /**
         * Returns the parsed time, if the user provided one.
         *
         * @return time of day, or {@code null}
         */
        public LocalTime getTime() {
            return time;
        }
    }

    /**
     * Parses date input in yyyy-MM-dd or d/M/yyyy with optional HHmm time.
     *
     * @param input Date/time text entered by the user.
     * @return Parsed date and optional time.
     * @throws ThomasException If the input is empty or not a supported format.
     */
    public static ParsedDateTime parseDateTime(String input) throws ThomasException {
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            throw new ThomasException("Date/time cannot be empty.");
        }

        String[] parts = trimmed.split("\\s+");
        if (parts.length > 2) {
            throw new ThomasException(
                    "Broo wrong date/time format. Use yyyy-MM-dd or d/M/yyyy, optionally with HHmm.");
        }

        LocalDate date = parseDate(parts[0]);
        LocalTime time = null;

        if (parts.length == 2) {
            try {
                time = LocalTime.parse(parts[1], TIME_COMPACT);
            } catch (DateTimeParseException e) {
                throw new ThomasException("Broo wrong time format. Use HHmm, e.g. 1800.");
            }
        }

        return new ParsedDateTime(date, time);
    }

    /**
     * Parses a date in yyyy-MM-dd, d/M/yyyy, or MMM dd yyyy.
     *
     * @param input Date text entered by the user.
     * @return Parsed date.
     * @throws ThomasException If the input is not a supported date format.
     */
    public static LocalDate parseDate(String input) throws ThomasException {
        String trimmed = input.trim();

        try {
            return LocalDate.parse(trimmed, ISO_DATE);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDate.parse(trimmed, SLASH_DATE);
            } catch (DateTimeParseException e2) {
                try {
                    return LocalDate.parse(trimmed, LEGACY_DISPLAY_DATE);
                } catch (DateTimeParseException e3) {
                    throw new ThomasException("Broo wrong date format. Use yyyy-MM-dd or d/M/yyyy.");
                }
            }
        }
    }

    /**
     * Returns a date/time string for UI output.
     *
     * @param date Date to format.
     * @param time Time to format, or null for date only.
     */
    public static String toDisplayString(LocalDate date, LocalTime time) {
        if (time == null) {
            return date.format(DISPLAY_DATE);
        }
        String display = date.atTime(time).format(DISPLAY_DATE_TIME);
        return display.replace("AM", "am").replace("PM", "pm");
    }

    /**
     * Returns a date/time string for file storage.
     *
     * @param date Date to format.
     * @param time Time to format, or null for date only.
     */
    public static String toStorageString(LocalDate date, LocalTime time) {
        if (time == null) {
            return date.format(ISO_DATE);
        }
        return date.format(ISO_DATE) + " " + time.format(TIME_COMPACT);
    }
}
