package alex.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import alex.exception.AlexException;

/**
 * Parses dates entered by users and formats dates for display.
 */
public class DateParser {
    private static final String DATE_TIME_ERROR_MESSAGE = "I couldn't read that. Use yyyy-MM-dd "
            + "and optionally HHmm, like 2026-09-20 1830.";
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter
            .ofPattern("HHmm", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Parses a date in the ISO yyyy-MM-dd format.
     *
     * @param input date text entered by the user.
     * @return the parsed date.
     * @throws AlexException if the input is not a valid ISO date.
     */
    public static LocalDate parse(String input) throws AlexException {
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            throw new AlexException(
                    "That date didn't scan. Use yyyy-MM-dd, like 2026-09-20.");
        }
    }

    /**
     * Parses a date followed by an optional 24-hour time.
     *
     * @param input date and optional time in yyyy-MM-dd [HHmm] format.
     * @return parsed task date and optional time.
     * @throws AlexException if the date or time is missing, malformed, or impossible.
     */
    public static TaskDateTime parseTaskDateTime(String input) throws AlexException {
        String[] parts = input.trim().split("\\s+");

        try {
            if (parts.length == 1) {
                return new TaskDateTime(LocalDate.parse(parts[0]));
            }
            if (parts.length == 2) {
                return new TaskDateTime(
                        LocalDate.parse(parts[0]),
                        LocalTime.parse(parts[1], TIME_FORMAT));
            }
        } catch (DateTimeParseException e) {
            throw new AlexException(DATE_TIME_ERROR_MESSAGE);
        }

        throw new AlexException(DATE_TIME_ERROR_MESSAGE);
    }

    /**
     * Formats a date in the form Jan 1 2026.
     *
     * @param date date to format.
     * @return the formatted date.
     */
    public static String format(LocalDate date) {
        return date.format(DISPLAY_FORMAT);
    }

    /**
     * Formats a task date and its optional time for display.
     *
     * @param taskDateTime task date and optional time.
     * @return formatted date and time.
     */
    public static String format(TaskDateTime taskDateTime) {
        String formattedDate = format(taskDateTime.date());
        return taskDateTime.time()
                .map(time -> formattedDate + " " + time.format(TIME_FORMAT))
                .orElse(formattedDate);
    }

    /**
     * Formats a task date and its optional time for storage.
     *
     * @param taskDateTime task date and optional time.
     * @return date and optional time in yyyy-MM-dd [HHmm] format.
     */
    public static String formatForStorage(TaskDateTime taskDateTime) {
        String formattedDate = taskDateTime.date().toString();
        return taskDateTime.time()
                .map(time -> formattedDate + " " + time.format(TIME_FORMAT))
                .orElse(formattedDate);
    }
}
