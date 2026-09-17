package penny;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Handles formatting and parsing of date time strings.
 */
public class DateTime {
    private static final DateTimeFormatter FORMAT_DATE_TIME = DateTimeFormatter.ofPattern("dd MM yyyy HHmm");
    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd MM yyyy");
    private static final DateTimeFormatter PARSE_DATE_TIME = DateTimeFormatter.ofPattern("ddMMuuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter PARSE_DATE = DateTimeFormatter.ofPattern("ddMMuuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Returns dateTime formatted as a string.
     *
     * @param dateTime date time to be formatted as a string.
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(FORMAT_DATE);
        }
        return dateTime.format(FORMAT_DATE_TIME);
    }

    /**
     * Returns dateTime obtained from parsing a string.
     *
     * @param input string to be parsed into a date.
     * @throws PennyException if the string format is not supported.
     */
    public static LocalDateTime parse(String input) throws PennyException {
        if (input == null) {
            throw new PennyException("Use 'ddMMyyyy HHmm' or 'ddMMyyyy'.");
        }
        String normalized = input.strip().replaceAll("\\s+", " ");
        if (!normalized.matches("[0-9]{8}( [0-9]{4})?") || normalized.substring(4, 8).equals("0000")) {
            throw new PennyException("Use 'ddMMyyyy HHmm' or 'ddMMyyyy'.");
        }
        try {
            return LocalDateTime.parse(normalized, PARSE_DATE_TIME);
        } catch (DateTimeParseException e1) {
            try {
                LocalDate date = LocalDate.parse(normalized, PARSE_DATE);
                return date.atStartOfDay(); // Midnight as the default time
            } catch (DateTimeParseException e2) {
                throw new PennyException("Use 'ddMMyyyy HHmm' or 'ddMMyyyy'.");
            }
        }
    }
}
