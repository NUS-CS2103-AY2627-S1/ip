package dave.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import dave.exception.DaveCommandException;

/**
 * Parses date and date-time strings into ParsedDateTime objects.
 * Supports multiple standard formats including yyyy-MM-dd HH:mm and yyyy-MM-dd with strict validation.
 */
public class DateTimeParser {

    /** Regex pattern matching yyyy-MM-dd format with optional HH:mm or HHmm. */
    private static final String DATE_TIME_SYNTAX_REGEX = "^\\d{4}-\\d{2}-\\d{2}( \\d{2}:?\\d{2})?$";

    /** Supported date-time formatters for parsing using strict calendar resolution. */
    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = new DateTimeFormatter[] {
        DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT)
    };

    /** Strict formatter for date-only input. */
    private static final DateTimeFormatter DATE_ONLY_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    /**
     * Parses a date or date-time string into a ParsedDateTime object.
     * Rejects invalid formats and non-existent calendar dates (e.g., Feb 30, Apr 31).
     *
     * @param input Raw date or date-time string.
     * @return Parsed date-time along with a flag indicating if time was specified.
     * @throws DaveCommandException If the string is invalid or represents a non-existent calendar date.
     */
    public static ParsedDateTime parse(String input) {
        String trimmed = input.trim();

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                LocalDateTime ldt = LocalDateTime.parse(trimmed, formatter);
                return new ParsedDateTime(ldt, true);
            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }

        try {
            LocalDate ld = LocalDate.parse(trimmed, DATE_ONLY_FORMATTER);
            return new ParsedDateTime(ld.atStartOfDay(), false);
        } catch (DateTimeParseException ignored) {
            // Not a date-only string either
        }

        if (trimmed.matches(DATE_TIME_SYNTAX_REGEX)) {
            throw new DaveCommandException("Pardon, Sire! Invalid date: date does not exist on the calendar "
                    + "(e.g., Feb 30, Apr 31, or invalid leap year).");
        }

        throw new DaveCommandException("Pardon, Sire! Date must be in yyyy-MM-dd or yyyy-MM-dd HH:mm format "
                + "(e.g., 2019-10-15 or 2005-12-22 16:00).");
    }
}
