package windy.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Parses task dates using Windy's supported input format.
 */
public final class TaskDateParser {
    public static final String INVALID_DATE_MESSAGE = "Invalid Date. Use yyyy-M-d";

    private static final DateTimeFormatter INPUT_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-M-d").withResolverStyle(ResolverStyle.STRICT);

    private TaskDateParser() {
    }

    /**
     * Parses a date using the supported {@code yyyy-M-d} input format.
     *
     * @param dateText the date text to parse.
     * @return the parsed date.
     * @throws DateTimeParseException if the date is invalid.
     */
    public static LocalDate parse(String dateText) {
        return LocalDate.parse(dateText, INPUT_FORMATTER);
    }
}
