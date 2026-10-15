package bot67.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/** Parses and formats the ISO date/time values supported by Level 8. */
public final class DateTimeParser {
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern("MMM d yyyy HH:mm");
    private static final DateTimeFormatter INPUT_DATE_TIME = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);

    /** Prevents construction of this utility class. */
    private DateTimeParser() {
        // Utility class; do not instantiate.
    }

    /** Parses an ISO date or date-time into a date-time value. */
    public static LocalDateTime parse(String value) {
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDateTime.parse(value, INPUT_DATE_TIME);
            } catch (DateTimeParseException ignoredAgain) {
                return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
            }
        }
    }

    /** Returns a user-friendly representation of a parsed date/time. */
    public static String format(LocalDateTime value, boolean hasTime) {
        return value.format(hasTime ? DISPLAY_DATE_TIME : DISPLAY_DATE);
    }
}
