package computa.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Utility methods for parsing and displaying the date formats accepted by Computa. */
public final class DateTimeParser {
    /** The input and storage format for a date without a time. */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    /** The input and storage format for a date with a four-digit time. */
    private static final DateTimeFormatter COMPACT_TIME_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    /** An alternative input format with a colon between hour and minute. */
    private static final DateTimeFormatter COLON_TIME_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);
    /** Input format for dates written as day/month/year. */
    private static final DateTimeFormatter SLASH_DATE_FORMAT = DateTimeFormatter.ofPattern("d/M/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    /** Input format for day/month/year followed by a four-digit time. */
    private static final DateTimeFormatter SLASH_COMPACT_TIME_FORMAT = DateTimeFormatter
            .ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT);
    /** Input format for day/month/year followed by a colon-separated time. */
    private static final DateTimeFormatter SLASH_COLON_TIME_FORMAT = DateTimeFormatter
            .ofPattern("d/M/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);
    /** A readable format used when displaying dates to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd uuuu",
            Locale.ENGLISH);
    /** A readable format used when displaying date-times to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MMM dd uuuu HHmm",
            Locale.ENGLISH);

    private DateTimeParser() {
        // Utility class; do not instantiate.
    }

    /**
     * Parses an ISO date or one of the supported date-time formats.
     *
     * @param text date or date-time entered by the user.
     * @return the parsed value, or {@code null} for a free-form value.
     */
    public static LocalDateTime parse(String text) {
        if (text == null) {
            return null;
        }
        String value = text.trim();
        LocalDateTime parsedDateTime = parseDateTime(value,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                COMPACT_TIME_FORMAT,
                COLON_TIME_FORMAT,
                SLASH_COMPACT_TIME_FORMAT,
                SLASH_COLON_TIME_FORMAT);
        if (parsedDateTime != null) {
            return parsedDateTime;
        }
        try {
            return LocalDate.parse(value, DATE_FORMAT).atStartOfDay();
        } catch (DateTimeParseException exception) {
            try {
                return LocalDate.parse(value, SLASH_DATE_FORMAT).atStartOfDay();
            } catch (DateTimeParseException slashException) {
                return null;
            }
        }
    }

    /**
     * Tries the supplied date-time formatters in order until one succeeds.
     *
     * @param value trimmed date-time text.
     * @param formatters ordered fallback formatters.
     * @return the parsed date-time, or {@code null} if all formatters reject it.
     */
    private static LocalDateTime parseDateTime(String value, DateTimeFormatter... formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException exception) {
                // Try the next formatter.
            }
        }
        return null;
    }

    /** Returns whether a value looks like an ISO date and should not be treated as free-form text. */
    public static boolean looksLikeDate(String text) {
        if (text == null) {
            return false;
        }
        return text.trim().matches("(\\d{4}-\\d{2}-\\d{2}|\\d{1,2}/\\d{1,2}/\\d{4}).*");
    }

    /** Parses the date used by the {@code on} command. */
    public static LocalDate parseQueryDate(String text) {
        if (text == null) {
            return null;
        }
        try {
            return LocalDate.parse(text.trim(), DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            try {
                return LocalDate.parse(text.trim(), SLASH_DATE_FORMAT);
            } catch (DateTimeParseException slashException) {
                return null;
            }
        }
    }

    /** Returns whether the original value contains a time component. */
    public static boolean hasTime(String text) {
        if (text == null) {
            return false;
        }
        String value = text.trim();
        return value.contains("T") || value.matches("(\\d{4}-\\d{2}-\\d{2}|\\d{1,2}/\\d{1,2}/\\d{4}) \\d{2}:?\\d{2}");
    }

    /** Formats a parsed value for display. */
    public static String formatForDisplay(LocalDateTime value, boolean includeTime) {
        return includeTime
                ? value.format(DISPLAY_DATE_TIME_FORMAT)
                : value.format(DISPLAY_DATE_FORMAT);
    }

    /** Formats a query date for a friendly heading. */
    public static String formatDateForDisplay(LocalDate value) {
        return value.format(DISPLAY_DATE_FORMAT);
    }

    /** Formats a parsed value for storage so that it can be loaded again. */
    public static String formatForStorage(LocalDateTime value, boolean includeTime) {
        return includeTime
                ? value.format(COMPACT_TIME_FORMAT)
                : value.format(DATE_FORMAT);
    }
}
