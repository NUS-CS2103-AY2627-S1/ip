package blud.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import blud.exception.DateTimeInputException;

/** Provides the date formats used by command parsing, display, and storage. */
public final class DateUtils {
    // Matches input like "2/12/2019 1800" or "02/12/2019 1800"
    // 'd' and 'M' single letters allow for single-digit days/months
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("d/M/uuuu HHmm").
            withResolverStyle(ResolverStyle.STRICT);

    // Outputs exactly: "Dec 02 2019, 6:00 PM"
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

    /** Parses a user-entered date and time, rejecting invalid calendar dates. */
    public static LocalDateTime parseInput(String input) {
        if (input == null || input.isBlank()) {
            throw new DateTimeInputException(
                    "Missing date and time. Please use d/M/yyyy HHmm (e.g., 2/12/2019 1800)");
        }
        try {
            return LocalDateTime.parse(input.trim(), INPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new DateTimeInputException(
                    "Invalid date or time. Please use a real date in d/M/yyyy HHmm format "
                            + "(e.g., 2/12/2019 1800)");
        }
    }

    /** Formats a date and time for persistent storage. */
    public static String formatForStorage(LocalDateTime dateTime) {
        assert dateTime != null : "A stored date must have been parsed successfully";
        return dateTime.format(INPUT_FORMATTER);
    }

    /** Formats a date and time for display to the user. */
    public static String formatOutput(LocalDateTime dateTime) {
        assert dateTime != null : "A displayed date must have been parsed successfully";
        return dateTime.format(OUTPUT_FORMATTER);
    }
}
