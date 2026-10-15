package edith.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Converts task dates between Edith's command, display, and saved-file formats. */
public final class DateFormatter {
    /** The ISO date format accepted in commands. */
    private static final DateTimeFormatter DATE_INPUT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    /** The ISO date-time format accepted in commands. */
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    /** The readable date format used in task lists and saved tasks. */
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT);
    /** The readable date-time format used in task lists and saved tasks. */
    private static final DateTimeFormatter DATE_TIME_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm", Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT);

    private DateFormatter() {
    }

    /**
     * Parses a date or date-time supplied in a command.
     *
     * @param input a date in {@code yyyy-MM-dd} or {@code yyyy-MM-dd HHmm} format
     * @return the parsed date-time and whether the user supplied a time
     */
    public static ParsedDateTime parseInput(String input) {
        if (input.length() == 10) {
            return new ParsedDateTime(LocalDate.parse(input, DATE_INPUT_FORMAT).atStartOfDay(), false);
        }
        return new ParsedDateTime(LocalDateTime.parse(input, DATE_TIME_INPUT_FORMAT), true);
    }

    /**
     * Parses a date or date-time displayed in a saved task.
     *
     * @param displayedDate a date in {@code MMM dd yyyy} or {@code MMM dd yyyy HH:mm} format
     * @return the parsed date-time and whether the saved value includes a time
     */
    public static ParsedDateTime parseDisplayedDateTime(String displayedDate) {
        if (displayedDate.length() == 11) {
            return new ParsedDateTime(LocalDate.parse(displayedDate, DATE_DISPLAY_FORMAT).atStartOfDay(), false);
        }
        return new ParsedDateTime(LocalDateTime.parse(displayedDate, DATE_TIME_DISPLAY_FORMAT), true);
    }

    /**
     * Formats a date or date-time for task-list output.
     *
     * @param dateTime the date-time to format
     * @param hasTime whether to include the time in the output
     * @return the readable date or date-time
     */
    public static String formatForDisplay(LocalDateTime dateTime, boolean hasTime) {
        return dateTime.format(hasTime ? DATE_TIME_DISPLAY_FORMAT : DATE_DISPLAY_FORMAT);
    }

    /** Holds a parsed date-time and records whether the original input included a time. */
    public record ParsedDateTime(LocalDateTime value, boolean hasTime) {
    }
}
