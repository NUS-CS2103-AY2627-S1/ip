package spoon.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import spoon.exception.InvalidFormatException;

/**
 * Handles the formatting of the date (+ time) user inputs and display outputs.
 */
public class DateFormat {
    /**
     * Stores both the date (+ time) and a boolean representing if time is included as a single data structure.
     */
    public record ParseResult(LocalDateTime dateTime, boolean includeTime) {}

    // Date input format handlers
    private static final DateTimeFormatter DATE_INPUT = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_TIME_INPUT = DateTimeFormatter.ofPattern("dd/MM/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);

    // Date output format handlers
    private static final DateTimeFormatter DATE_DISPLAY = DateTimeFormatter.ofPattern("MMM dd uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_TIME_DISPLAY = DateTimeFormatter.ofPattern("MMM dd uuuu, h:mma")
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Parses a string into a LocalDate or LocalDateTime.
     * Supports both "dd/MM/yyyy" and "dd/MM/yyyy HHmm".
     *
     * @param input the date to be parsed.
     * @return a ParseResult containing both the date (+ time) and a boolean representing if time is included.
     */
    public static ParseResult parse(String input) throws InvalidFormatException {
        // Parsing with date and time
        try {
            LocalDateTime dateTime = LocalDateTime.parse(input.trim(), DATE_TIME_INPUT);
            return new ParseResult(dateTime, true);
        } catch (DateTimeParseException ignored) {
            // Fall through
        }
        // Parsing with date only
        try {
            LocalDate date = LocalDate.parse(input.trim(), DATE_INPUT);
            return new ParseResult(date.atStartOfDay(), false);
        } catch (DateTimeParseException e) {
            throw new InvalidFormatException("date/time (as dd/MM/yyyy or dd/MM/yyyy HHmm)");
        }
    }

    /**
     * Converts a LocalDateTime to a string for display.
     *
     * @param parsedDateTime date (+ time) and a boolean representing if time is included.
     * @return date (+ time) converted to string, in display format.
     */
    public static String toDisplay(ParseResult parsedDateTime) {
        if (parsedDateTime.includeTime()) {
            return parsedDateTime.dateTime().format(DATE_TIME_DISPLAY)
                    .replace("AM", "am").replace("PM", "pm");
        }
        return parsedDateTime.dateTime().format(DATE_DISPLAY);
    }

    /**
     * Converts a LocalDate to a string for display.
     *
     * @param date date (+ time) and a boolean representing if time is included.
     * @return date (+ time) converted to string, in display format.
     */
    public static String toDisplay(LocalDate date) {
        return date.format(DATE_DISPLAY);
    }

    /**
     * Converts a LocalDateTime to a string for storage.
     *
     * @param parsedDateTime date (+ time) and a boolean representing if time is included.
     * @return date (+ time) converted to string, in storage (input) format.
     */
    public static String toStorage(ParseResult parsedDateTime) {
        if (parsedDateTime.includeTime()) {
            return parsedDateTime.dateTime().format(DATE_TIME_INPUT);
        }
        return parsedDateTime.dateTime().format(DATE_INPUT);
    }

    /**
     * Evaluates if a deadline is due on a specific date.
     *
     * @param deadline date (+ time) of the deadline task.
     * @param targetDate date being queried.
     * @return true if deadline is due on the same day as target date, else false.
     */
    public static boolean isDueOn(LocalDateTime deadline, LocalDate targetDate) {
        return deadline.toLocalDate().isEqual(targetDate);
    }

    /**
     * Evaluates if an event is due on a specific date.
     *
     * @param eventStart start date (+ time) of the event task.
     * @param eventEnd end date (+ time) of the event task.
     * @param targetDate date being queried.
     * @return true if deadline is due on the same day as target date, else false.
     */
    public static boolean isOccurringOn(LocalDateTime eventStart, LocalDateTime eventEnd, LocalDate targetDate) {
        return !targetDate.isBefore(eventStart.toLocalDate()) && !targetDate.isAfter(eventEnd.toLocalDate());
    }

    /**
     * Evaluates if a task is due by a specific date.
     *
     * @param taskDate date (+ time) of the deadline task.
     * @param targetDate date being queried.
     * @return true if deadline is due on the same day as target date, else false.
     */
    public static boolean isDueBy(LocalDateTime taskDate, LocalDate targetDate) {
        return !taskDate.toLocalDate().isAfter(targetDate);
    }
}
