package answerme.parser;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import answerme.exception.AnswerMeException;

/**
 * Parses strings into datetime values and formats datetime values for display.
 */
public class DateTimeParser {
    private static final DateTimeFormatter DISPLAY_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d yyyy HH:mm", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private static final String INVALID_DATE_TIME_MESSAGE =
            "Ensure that date/time is formatted correctly.\n"
            + "Use a date such as 11/9/2026 or a date and time such as 11/9/2026 1800.";

    private static final List<DateTimeFormatter> INPUT_FORMATTERS = List.of(
            createInputFormatter("uuuu-M-d"),
            createInputFormatter("uuuu/M/d"),
            createInputFormatter("d-M-uuuu"),
            createInputFormatter("d/M/uuuu"),
            createInputFormatter("MMM d uuuu"),
            createInputFormatter("MMM d uuuu HH:mm"),
            createInputFormatter("d/M/uuuu HHmm"),
            createInputFormatter("d-M-uuuu HHmm"),
            createInputFormatter("uuuu-M-d HH:mm"),
            createInputFormatter("uuuu/M/d HH:mm"),
            createInputFormatter("d-M-uuuu HH:mm"),
            createInputFormatter("d/M/uuuu HH:mm")
    );

    private DateTimeParser() {
    }

    /**
     * Parses a raw date/datetime string into a {@code LocalDateTime} object.
     * A string without a specified time is interpreted as the start of the day.
     *
     * @param rawDateTime The date or datetime string to parse.
     * @return The parsed datetime value.
     * @throws AnswerMeException If the input does not match a supported date or time format.
     */
    public static LocalDateTime parseDateTime(String rawDateTime) throws AnswerMeException {
        if (rawDateTime == null || rawDateTime.isBlank()) {
            throw new AnswerMeException(INVALID_DATE_TIME_MESSAGE);
        }

        String trimmedDateTime = rawDateTime.trim();

        for (DateTimeFormatter formatter : INPUT_FORMATTERS) {
            Optional<LocalDateTime> parsedDateTime = parseDateTimeHelper(trimmedDateTime, formatter);

            if (parsedDateTime.isPresent()) {
                return parsedDateTime.get();
            }
        }
        throw new AnswerMeException(INVALID_DATE_TIME_MESSAGE);
    }

    /**
     * Formats a datetime value for display, omitting its time when it is midnight.
     *
     * @param dateTime The datetime value to format.
     * @return The formatted date or datetime string.
     * @throws NullPointerException If {@code dateTime} is null.
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new NullPointerException("Datetime cannot be null.");
        }
        //@@author RyanNgCT-reused
        //Code reused from https://github.com/draxche/ip/blob/master/src/main/java/drax/ScheduleDateTime.java
        //with minor modifications (by my teammate Kai Le @draxche)
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(DISPLAY_DATE_FORMATTER);
        }
        return dateTime.format(DISPLAY_DATETIME_FORMATTER);
        //@@author
    }

    /**
     * Creates a strict formatter for the specified datetime pattern.
     * Defaults missing hour and minute fields to midnight.
     *
     * @param input The pattern used to parse datetime values.
     * @return A formatter configured with the specified pattern.
     */
    private static DateTimeFormatter createInputFormatter(String input) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(input)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Attempts to parse a datetime value using the specified formatter.
     *
     * @param dateTime The datetime string to parse.
     * @param formatter The formatter used for parsing.
     * @return An optional containing the parsed datetime, or an empty
     *         optional if parsing fails.
     */
    private static Optional<LocalDateTime> parseDateTimeHelper(
            String dateTime, DateTimeFormatter formatter) {
        try {
            return Optional.of(LocalDateTime.parse(dateTime, formatter));
        } catch (DateTimeParseException exception) {
            return Optional.empty();
        }
    }
}
