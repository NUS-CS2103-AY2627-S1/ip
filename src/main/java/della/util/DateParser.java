package della.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Converts between date-time strings and {@link LocalDateTime} objects.
 */
public class DateParser {
    private DateParser() {
    }

    /**
     * Returns the date and time parsed from a string using the specified pattern.
     *
     * @param dateString Text containing a date and time.
     * @param datePattern Pattern used to parse the date and time.
     * @return Parsed date and time.
     * @throws DateTimeParseException If the date string does not match the pattern.
     */
    public static LocalDateTime parseDateTime(String dateString, String datePattern) throws DateTimeParseException {
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern(datePattern);
        return LocalDateTime.parse(dateString, inputFormat);
    }

    /**
     * Returns the date and time formatted for display and storage.
     *
     * @param dateTime Date and time to format.
     * @return Date and time in {@code MMM dd yyyy h:mma} format.
     */
    public static String printDateTime(LocalDateTime dateTime) {
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("MMM dd yyyy h:mma");
        return dateTime.format(outputFormat);
    }
}
