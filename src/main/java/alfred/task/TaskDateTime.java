package alfred.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import alfred.exception.AlfredException;

/**
 * Parses and formats the dates and times used by deadline and event tasks.
 */
public final class TaskDateTime {
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMAT = DateTimeFormatter
            .ofPattern("d/M/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd uuuu");
    private static final DateTimeFormatter DATE_TIME_OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm");

    /** Prevents construction of a utility class. */
    private TaskDateTime() {
    }

    /**
     * Parses a date-only or date-and-time value entered for a task.
     *
     * @param input value in {@code yyyy-MM-dd} or {@code d/M/yyyy HHmm} format
     * @return the parsed date and time, with date-only values set to midnight
     * @throws AlfredException if the input does not match a supported valid format
     */
    public static LocalDateTime parse(String input) throws AlfredException {
        try {
            return LocalDate.parse(input).atStartOfDay();
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(input, DATE_TIME_INPUT_FORMAT);
            } catch (DateTimeParseException ignored) {
                throw new AlfredException("Alfred needs a valid date in yyyy-MM-dd or d/M/yyyy HHmm format.");
            }
        }
    }

    /**
     * Parses an ISO-8601 date and time previously written to task storage.
     *
     * @param input stored date and time
     * @return the parsed date and time
     * @throws AlfredException if the stored date and time is invalid
     */
    public static LocalDateTime parseStored(String input) throws AlfredException {
        try {
            return LocalDateTime.parse(input);
        } catch (DateTimeParseException e) {
            throw new AlfredException("Alfred could not load the saved tasks.");
        }
    }

    /**
     * Formats a task date and time for display.
     *
     * @param dateTime date and time to display
     * @return the date, with the time shown unless it is midnight
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime.toLocalTime().equals(java.time.LocalTime.MIDNIGHT)) {
            return dateTime.format(DATE_OUTPUT_FORMAT);
        }
        return dateTime.format(DATE_TIME_OUTPUT_FORMAT);
    }
}
