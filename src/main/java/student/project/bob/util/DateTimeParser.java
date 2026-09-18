package student.project.bob.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.Locale;

/**
 * Parses and formats the dates and times used by deadline and event tasks.
 */
public final class DateTimeParser {
    private static final DateTimeFormatter SPACE_SEPARATED_DATE_TIME_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("uuuu-MM-dd HH:mm")
            .optionalStart()
            .appendPattern(":ss")
            .optionalEnd()
            .toFormatter(Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEE, MMM dd uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("EEE, MMM dd uuuu HH:mm", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_PARSE_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("EEE, MMM dd uuuu HH:mm")
            .optionalStart()
            .appendPattern(":ss")
            .optionalEnd()
            .toFormatter(Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_WITH_SECONDS_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("EEE, MMM dd uuuu HH:mm")
            .appendPattern(":ss")
            .toFormatter(Locale.ENGLISH);

    private DateTimeParser() {
        // Prevent instantiation of this utility class.
    }

    /**
     * Parses a date or local date-time string.
     *
     * @param input ISO date or local date-time string
     * @return a {@link LocalDate} when no time is supplied, otherwise a {@link LocalDateTime}
     * @throws DateTimeParseException if the input is not a supported date or time
     */
    public static Temporal parse(String input) {
        String trimmedInput = input.trim();

        try {
            return LocalDateTime.parse(trimmedInput, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            // Try the other supported representations.
        }

        try {
            return LocalDateTime.parse(trimmedInput, SPACE_SEPARATED_DATE_TIME_FORMAT);
        } catch (DateTimeParseException ignored) {
            // Try a date-only representation.
        }

        try {
            return LocalDate.parse(trimmedInput, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {
            // Saved tasks use the display representation, so try that next.
        }

        try {
            return LocalDateTime.parse(trimmedInput, DISPLAY_DATE_TIME_PARSE_FORMAT);
        } catch (DateTimeParseException ignored) {
            // Try the display representation for a date without a time.
        }

        return LocalDate.parse(trimmedInput, DISPLAY_DATE_FORMAT);
    }

    /**
     * Formats a stored date or local date-time for display and persistence.
     *
     * @param temporal date or local date-time to format
     * @return formatted date or local date-time
     * @throws IllegalArgumentException if the value is not a supported temporal type
     */
    public static String format(Temporal temporal) {
        if (temporal instanceof LocalDateTime dateTime) {
            if (dateTime.getSecond() == 0 && dateTime.getNano() == 0) {
                return DISPLAY_DATE_TIME_FORMAT.format(dateTime);
            }
            return DISPLAY_DATE_TIME_WITH_SECONDS_FORMAT.format(dateTime);
        }
        if (temporal instanceof LocalDate date) {
            return DISPLAY_DATE_FORMAT.format(date);
        }
        throw new IllegalArgumentException("Unsupported date or time type: " + temporal.getClass());
    }
}
