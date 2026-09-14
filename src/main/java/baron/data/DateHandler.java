// javadocs created by AI

package baron.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;

import baron.exception.BaronException;

/**
 * A utility class for handling date and time parsing and formatting.
 */
public class DateHandler {
    /**
     * The output format for displaying dates and times in the application.
     */
    private static final DateTimeFormatter OUTPUT = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");

    // Ai suggested use of withResolverStyle.STRICT to ensure strict parsing of date and time formats
    /**
     * The input format for parsing dates and times from user input.
     */
    private static final DateTimeFormatter INPUT = new DateTimeFormatterBuilder()
            .optionalStart()
                .appendPattern("[uuuu-MM-dd][dd-MM-uuuu][uuuu/MM/dd][dd/MM/uuuu][MMM dd uuuu][MM dd uuuu]")
                .appendPattern("[ HH:mm:ss][ HH:mm]")
            .optionalEnd()
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .toFormatter().withResolverStyle(ResolverStyle.STRICT);

    /**
     * Parses a date string into a LocalDateTime object using the defined input format.
     *
     * @param dateStr the date string to parse
     * @return the parsed LocalDateTime object
     */
    public static LocalDateTime parse(String dateStr) throws BaronException {
        try {
            return LocalDateTime.parse(dateStr, INPUT);
        } catch (Exception e) {
            throw new BaronException("That date cannot be read, traveler. Try yyyy-MM-dd, dd-MM-yyyy, "
                    + "yyyy/MM/dd, dd/MM/yyyy, MMM dd yyyy, or MM dd yyyy, with an optional HH:mm time.");
        }
    }

    /**
     * Formats a LocalDateTime object into a string using the defined output format.
     *
     * @param dateTime the LocalDateTime object to format
     * @return the formatted date string
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(OUTPUT);
    }
}
