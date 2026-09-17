package mouse.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

import mouse.MouseException;

/**
 * A date, date-time, or free-text time value.
 */
public class ParsedWhen {
    private static final DateTimeFormatter DATE_IN =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_TIME_IN =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_OUT =
            DateTimeFormatter.ofPattern("MMM dd uuuu");
    private static final DateTimeFormatter DATE_TIME_OUT =
            DateTimeFormatter.ofPattern("MMM dd uuuu, h:mma");
    private static final DateTimeFormatter DATE_IN_SLASH =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_TIME_IN_SLASH =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT);
    private static final Pattern ISO_LIKE =
            Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}(?:\\s+\\d{4})?");
    private static final Pattern SLASH_LIKE =
            Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{4}(?:\\s+\\d{4})?");

    private final LocalDateTime dateTime;
    private final LocalDate date;
    private final String text;

    /**
     * Creates a parsed time that holds exactly one of date-time, date, or free text.
     *
     * @param dateTime Parsed date-time, or {@code null}.
     * @param date Parsed date, or {@code null}.
     * @param text Free-text value, or {@code null}.
     */
    private ParsedWhen(LocalDateTime dateTime, LocalDate date, String text) {
        this.dateTime = dateTime;
        this.date = date;
        this.text = text;
    }

    /**
     * Parses {@code raw} as a date-time, date, or free-text value.
     *
     * @param raw User-supplied time text.
     * @param fieldName Label used in error messages, such as {@code The '/by' time}.
     * @return Parsed value.
     * @throws MouseException If {@code raw} looks like a date but is invalid.
     */
    public static ParsedWhen parse(String raw, String fieldName) throws MouseException {
        LocalDateTime parsedDateTime = tryDateTime(raw);
        if (parsedDateTime != null) {
            return new ParsedWhen(parsedDateTime, null, null);
        }
        LocalDate parsedDate = tryDate(raw);
        if (parsedDate != null) {
            return new ParsedWhen(null, parsedDate, null);
        }
        if (looksLikeDate(raw)) {
            throw new MouseException(fieldName + " looks like a date but is not a real one GRR");
        }
        return new ParsedWhen(null, null, raw);
    }

    /**
     * Returns whether both values are real dates and can be ordered.
     *
     * @param other Other time value.
     * @return {@code true} if both are dated.
     */
    public boolean canCompareTo(ParsedWhen other) {
        return !isFreeText() && !other.isFreeText();
    }

    /**
     * Compares this value to {@code other} when both are dated.
     *
     * @param other Other time value.
     * @return Negative, zero, or positive as in {@link Comparable#compareTo(Object)}.
     */
    public int compareTo(ParsedWhen other) {
        return toDateTime().compareTo(other.toDateTime());
    }

    /**
     * Returns the display form used in replies.
     *
     * @return Formatted date or the original free text.
     */
    public String toDisplayString() {
        if (dateTime != null) {
            return dateTime.format(DATE_TIME_OUT);
        }
        if (date != null) {
            return date.format(DATE_OUT);
        }
        return text;
    }

    /**
     * Returns a form {@link #parse(String, String)} can read again.
     *
     * @return ISO date, ISO date-time, or the original free text.
     */
    public String toSaveString() {
        if (dateTime != null) {
            return dateTime.format(DATE_TIME_IN);
        }
        if (date != null) {
            return date.format(DATE_IN);
        }
        return text;
    }

    /**
     * Returns whether this value is undated free text.
     *
     * @return {@code true} if no calendar date was parsed.
     */
    private boolean isFreeText() {
        return text != null;
    }

    /**
     * Returns this value as a date-time for ordering.
     * Dates without a time use the start of that day.
     *
     * @return Comparable date-time.
     */
    private LocalDateTime toDateTime() {
        if (dateTime != null) {
            return dateTime;
        }
        return date.atStartOfDay();
    }

    /**
     * Returns whether {@code raw} matches an ISO or slash date pattern.
     *
     * @param raw User-supplied time text.
     * @return {@code true} if the text looks like a date.
     */
    private static boolean looksLikeDate(String raw) {
        return ISO_LIKE.matcher(raw).matches() || SLASH_LIKE.matcher(raw).matches();
    }

    /**
     * Tries to parse {@code raw} as a date-time.
     *
     * @param raw User-supplied time text.
     * @return Parsed date-time, or {@code null}.
     */
    private static LocalDateTime tryDateTime(String raw) {
        return parseDateTime(raw, DATE_TIME_IN, DATE_TIME_IN_SLASH);
    }

    /**
     * Tries to parse {@code raw} as a date.
     *
     * @param raw User-supplied time text.
     * @return Parsed date, or {@code null}.
     */
    private static LocalDate tryDate(String raw) {
        return parseDate(raw, DATE_IN, DATE_IN_SLASH);
    }

    /**
     * Tries each date-time formatter until one accepts {@code raw}.
     *
     * @param raw User-supplied time text.
     * @param formatters Formatters to try in order.
     * @return Parsed date-time, or {@code null}.
     */
    private static LocalDateTime parseDateTime(String raw, DateTimeFormatter... formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(raw, formatter);
            } catch (DateTimeParseException exception) {
                // Try the next formatter.
            }
        }
        return null;
    }

    /**
     * Tries each date formatter until one accepts {@code raw}.
     *
     * @param raw User-supplied time text.
     * @param formatters Formatters to try in order.
     * @return Parsed date, or {@code null}.
     */
    private static LocalDate parseDate(String raw, DateTimeFormatter... formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(raw, formatter);
            } catch (DateTimeParseException exception) {
                // Try the next formatter.
            }
        }
        return null;
    }
}
