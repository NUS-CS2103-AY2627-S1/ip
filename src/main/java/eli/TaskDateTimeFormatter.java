package eli;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/** Formats the supported task date and time inputs for display. */
final class TaskDateTimeFormatter {
    private static final DateTimeFormatter INPUT_DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter OUTPUT_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM d yyyy, h:mma");
    private static final DateTimeFormatter INPUT_DATE =
            DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter OUTPUT_DATE =
            DateTimeFormatter.ofPattern("MMM d yyyy");

    private TaskDateTimeFormatter() {
        // Utility class should not be instantiated.
    }

    /** Formats a supported date-time, preserving other user-entered text. */
    static String formatDateTime(String value) {
        return tryFormatDateTime(value).orElse(value);
    }

    /** Formats a supported date or date-time, preserving other user-entered text. */
    static String formatDateOrDateTime(String value) {
        return tryFormatDateTime(value)
                .or(() -> tryFormatDate(value))
                .orElse(value);
    }

    /** Parses a supported input date-time. */
    static Optional<LocalDateTime> parseInputDateTime(String value) {
        try {
            return Optional.of(LocalDateTime.parse(value, INPUT_DATE_TIME));
        } catch (DateTimeParseException ignored) {
            return Optional.empty();
        }
    }

    /** Returns whether a date-like value uses a supported shape but is invalid. */
    static boolean isInvalidDateOrDateTime(String value) {
        boolean looksLikeDate = value.matches("\\d{4}-\\d{2}-\\d{2}");
        boolean looksLikeDateTime = value.matches("\\d{4}-\\d{2}-\\d{2} \\d{4}");
        return looksLikeDate && tryFormatDate(value).isEmpty()
                || looksLikeDateTime && parseInputDateTime(value).isEmpty();
    }

    /** Returns whether a date-time-like value has an impossible date or time. */
    static boolean isInvalidDateTime(String value) {
        return value.matches("\\d{4}-\\d{2}-\\d{2} \\d{4}")
                && parseInputDateTime(value).isEmpty();
    }

    /** Parses a displayed date-time into a value suitable for chronological sorting. */
    static Optional<LocalDateTime> parseDisplayDateTime(String value) {
        try {
            return Optional.of(LocalDateTime.parse(value, OUTPUT_DATE_TIME));
        } catch (DateTimeParseException ignored) {
            return Optional.empty();
        }
    }

    /** Parses a displayed date or date-time into a chronological sorting value. */
    static Optional<LocalDateTime> parseDisplayDateOrDateTime(String value) {
        Optional<LocalDateTime> dateTime = parseDisplayDateTime(value);
        if (dateTime.isPresent()) {
            return dateTime;
        }

        try {
            return Optional.of(LocalDate.parse(value, OUTPUT_DATE).atStartOfDay());
        } catch (DateTimeParseException ignored) {
            return Optional.empty();
        }
    }

    /** Returns a formatted date-time when the value has the supported input form. */
    private static Optional<String> tryFormatDateTime(String value) {
        return parseInputDateTime(value).map(dateTime -> dateTime.format(OUTPUT_DATE_TIME));
    }

    /** Returns a formatted date when the value has the supported input form. */
    private static Optional<String> tryFormatDate(String value) {
        try {
            return Optional.of(LocalDate.parse(value, INPUT_DATE).format(OUTPUT_DATE));
        } catch (DateTimeParseException ignored) {
            return Optional.empty();
        }
    }
}
