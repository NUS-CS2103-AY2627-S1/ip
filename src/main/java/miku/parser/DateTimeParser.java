package miku.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import miku.MikuException;

/** Parses and formats the date values accepted by Miku's scheduled tasks. */
public final class DateTimeParser {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter SLASH_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd uuuu");
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu h:mm a");

    /** Prevents construction of this utility class. */
    private DateTimeParser() {
    }

    /**
     * Parses a date, optionally followed by a 24-hour time, into a typed value.
     *
     * @throws MikuException if the input does not use a valid supported format
     */
    public static ParsedDateTime parse(String input) throws MikuException {
        try {
            if (input.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return new ParsedDateTime(LocalDateTime.parse(input + " 0000", DATE_TIME_FORMAT), false);
            }
            if (input.matches("\\d{4}-\\d{2}-\\d{2} \\d{4}")) {
                return new ParsedDateTime(LocalDateTime.parse(input, DATE_TIME_FORMAT), true);
            }
            if (input.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
                return new ParsedDateTime(LocalDateTime.parse(input + " 0000", SLASH_DATE_TIME_FORMAT), false);
            }
            if (input.matches("\\d{1,2}/\\d{1,2}/\\d{4} \\d{4}")) {
                return new ParsedDateTime(LocalDateTime.parse(input, SLASH_DATE_TIME_FORMAT), true);
            }
        } catch (DateTimeParseException exception) {
            throw invalidDateTime();
        }
        throw invalidDateTime();
    }

    /** Formats a typed date in the display format used by task list entries. */
    public static String format(LocalDateTime dateTime, boolean hasTime) {
        return (hasTime ? DISPLAY_DATE_TIME_FORMAT : DISPLAY_DATE_FORMAT).format(dateTime);
    }

    /** Ensures that an event end does not occur before its start. */
    public static void validateEventRange(LocalDateTime startDateTime, LocalDateTime endDateTime)
            throws MikuException {
        if (endDateTime.isBefore(startDateTime)) {
            throw new MikuException("The end of an event cannot be earlier than its start!! \u2728");
        }
    }

    /** Returns the error used consistently when a supplied date cannot be parsed. */
    private static MikuException invalidDateTime() {
        return new MikuException("Please use yyyy-MM-dd or d/M/yyyy, optionally followed by HHmm (24-hour time) ♪");
    }

    /** Holds a parsed date-time and whether the original input included a time. */
    public record ParsedDateTime(LocalDateTime dateTime, boolean hasTime) {
    }
}
