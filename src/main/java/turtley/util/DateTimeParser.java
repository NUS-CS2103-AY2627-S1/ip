package turtley.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.Temporal;
import java.util.List;

import turtley.exception.TurtleyException;

// Written with AI assistance.
/**
 * Parses and formats the date and date-time values accepted by Turtley.
 */
public final class DateTimeParser {

    /** The formats shown to users when a date/time value is invalid. */
    private static final String SUPPORTED_FORMATS =
            "yyyy-MM-dd, dd-MM-yyyy, yyyy/MM/dd, dd/MM/yyyy, yyyy-MM-dd HH:mm, "
                    + "dd-MM-yyyy HH:mm, yyyy/MM/dd HH:mm, dd/MM/yyyy HH:mm, "
                    + "yyyy-MM-dd HHmm, dd-MM-yyyy HHmm, yyyy/MM/dd HHmm, or dd/MM/yyyy HHmm";

    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            formatter("uuuu-MM-dd HH:mm"),
            formatter("dd-MM-uuuu HH:mm"),
            formatter("uuuu-MM-dd HHmm"),
            formatter("dd-MM-uuuu HHmm"),
            formatter("uuuu/MM/dd HH:mm"),
            formatter("dd/MM/uuuu HH:mm"),
            formatter("uuuu/MM/dd HHmm"),
            formatter("dd/MM/uuuu HHmm"),
            formatter("uuuu-MM-dd'T'HH:mm"),
            formatter("dd-MM-uuuu'T'HH:mm"),
            formatter("uuuu/MM/dd'T'HH:mm"),
            formatter("dd/MM/uuuu'T'HH:mm"));

    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            formatter("uuuu-MM-dd"),
            formatter("dd-MM-uuuu"),
            formatter("uuuu/MM/dd"),
            formatter("dd/MM/uuuu"));

    private static final DateTimeFormatter CANONICAL_DATE_TIME_FORMATTER = formatter("uuuu-MM-dd HH:mm");

    private static final DateTimeFormatter CANONICAL_DATE_FORMATTER = formatter("uuuu-MM-dd");

    private DateTimeParser() {
        // Utility class; do not create instances.
    }

    /**
     * Parses a date or date-time value into the most specific Java time type.
     *
     * @param value the user-entered value.
     * @return a {@link LocalDateTime} for values containing a time, otherwise a {@link LocalDate}
     * @throws TurtleyException if the value is blank or does not use a supported format.
     */
    public static Temporal parse(String value) {
        if (value == null || value.isBlank()) {
            throw invalidDateTime();
        }

        String trimmedValue = value.trim();
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(trimmedValue, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported date-time format.
            }
        }
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(trimmedValue, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported date format.
            }
        }
        throw invalidDateTime();
    }

    /**
     * Formats a parsed value in the canonical form used by the UI and save file.
     *
     * @param value the parsed date or date-time value.
     * @return the canonical ISO-like representation.
     * @throws TurtleyException if the value is not a supported Java time type.
     */
    public static String format(Temporal value) {
        if (value instanceof LocalDateTime dateTime) {
            return dateTime.format(CANONICAL_DATE_TIME_FORMATTER);
        }
        if (value instanceof LocalDate date) {
            return date.format(CANONICAL_DATE_FORMATTER);
        }
        throw new TurtleyException("Unable to format an unsupported date/time value.");
    }

    /**
     * Checks whether a task date/time occurs on or before a supplied cutoff.
     * A date-only cutoff includes every value on that calendar date, while a
     * date-time cutoff compares the time as well.
     *
     * @param value the task date or date-time.
     * @param cutoff the user-supplied cutoff date or date-time.
     * @return {@code true} if the value is on or before the cutoff.
     */
    public static boolean isOnOrBefore(Temporal value, Temporal cutoff) {
        if (value == null || cutoff == null) {
            return false;
        }
        if (cutoff instanceof LocalDate cutoffDate) {
            return toLocalDate(value).compareTo(cutoffDate) <= 0;
        }
        if (cutoff instanceof LocalDateTime cutoffDateTime) {
            LocalDateTime valueDateTime = value instanceof LocalDate date
                    ? date.atStartOfDay()
                    : (LocalDateTime) value;
            return !valueDateTime.isAfter(cutoffDateTime);
        }
        throw new TurtleyException("Unable to compare an unsupported date/time value.");
    }

    /**
     * Checks whether the first date/time occurs strictly before the second date/time.
     * Date-only values are treated as occurring at the start of their day.
     *
     * @param value the first date/time value.
     * @param other the second date/time value.
     * @return {@code true} if {@code value} occurs before {@code other}.
     */
    public static boolean isBefore(Temporal value, Temporal other) {
        if (value == null || other == null) {
            return false;
        }
        return toLocalDateTime(value).isBefore(toLocalDateTime(other));
    }

    /**
     * Extracts the calendar date from either supported Java time type.
     *
     * @param value a date or date-time value.
     * @return the calendar date represented by the value.
     */
    private static LocalDate toLocalDate(Temporal value) {
        if (value instanceof LocalDate date) {
            return date;
        }
        if (value instanceof LocalDateTime dateTime) {
            return dateTime.toLocalDate();
        }
        throw new TurtleyException("Unable to compare an unsupported date/time value.");
    }

    private static LocalDateTime toLocalDateTime(Temporal value) {
        if (value instanceof LocalDateTime dateTime) {
            return dateTime;
        }
        if (value instanceof LocalDate date) {
            return date.atStartOfDay();
        }
        throw new TurtleyException("Unable to compare an unsupported date/time value.");
    }

    /**
     * Creates the consistent user-facing parsing error.
     *
     * @return the parsing error.
     */
    private static TurtleyException invalidDateTime() {
        return new TurtleyException("Invalid date/time format. Use " + SUPPORTED_FORMATS + ".");
    }

    /**
     * Builds a strict formatter so invalid calendar dates are rejected.
     *
     * @param pattern the formatter pattern.
     * @return a strict formatter for the pattern.
     */
    private static DateTimeFormatter formatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.STRICT);
    }
}
