package sunny.utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.Optional;

public class DateParser {
    private DateParser() {}
    private static class Holder {
        private static final DateParser INSTANCE = new DateParser();
    }
    /**
     *
     * Returns the singleton instance of DateParser.
     *
     * @return the DateParser singleton instance
     */
    public static DateParser getInstance() {
        return Holder.INSTANCE;
    }

    private final DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HHmm");

    private static final DateTimeFormatter FLEXIBLE_FORMATTER = new DateTimeFormatterBuilder()
            // 1. Handle the date part variants
            .appendPattern("[dd/MM/uuuu][dd-MM-uuuu]")
            // 2. Handle the optional space and time part
            .appendPattern("[ HHmm]")
            // 3. Fallback to midnight if the time pattern is missing
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 23)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 59)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Parses a String input and returns an Optional object that either contains a LocalDateTime object if the input is
     * of an acceptable format, or an Empty object if not.
     * The acceptable formats are dd/mm/yyyy, dd-mm-yyyy, or either two with HHmm behind.
     * Throws an IllegalArgumentException if the input is null.
     *
     * @param inputDate the String to be parsed.
     * @return an Optional object containing a LocalDateTime object, or nothing.
     */
    public Optional<LocalDateTime> parse(String inputDate) {//dd/mm/yyyy, dd-mm-yyyy, either two with time
        if (inputDate == null) {
            throw new IllegalArgumentException("Input string for DateParser.parse cannot be null");
        }
        try {
            return Optional.of(LocalDateTime.parse(inputDate.trim(), FLEXIBLE_FORMATTER));
        } catch (Exception e) {
            return Optional.empty(); // Not a date format
        }
    }
}