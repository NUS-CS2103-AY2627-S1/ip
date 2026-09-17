package wobble.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Parses and displays date/time text used by deadlines and events. */
public final class DateTimeParser {
    private static final DateTimeFormatter[] DATE_INPUT_FORMATS = {
        strictFormatter("uuuu-MM-dd"),
        strictFormatter("uuuu.MM.dd"),
        strictFormatter("uuuu/MM/dd")
    };
    private static final DateTimeFormatter[] INPUT_FORMATS = {
        strictFormatter("uuuu-MM-dd HHmm"),
        strictFormatter("uuuu-MM-dd HH:mm"),
        strictFormatter("uuuu.MM.dd HHmm"),
        strictFormatter("uuuu.MM.dd HH:mm"),
        strictFormatter("uuuu/MM/dd HHmm"),
        strictFormatter("uuuu/MM/dd HH:mm"),
        DateTimeFormatter.ISO_LOCAL_DATE_TIME.withResolverStyle(ResolverStyle.STRICT)
    };
    private static final DateTimeFormatter DATE_OUTPUT = DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_OUTPUT = DateTimeFormatter.ofPattern(
            "MMM d yyyy h:mm a", Locale.ENGLISH);

    private DateTimeParser() {
    }

    /** Parses a date or date/time string into a LocalDateTime. */
    public static LocalDateTime parse(String value) {
        if (value == null || value.isBlank()) {
            throw new DateTimeParseException("A date/time value is required", "", 0);
        }
        String text = value.trim();
        for (DateTimeFormatter format : DATE_INPUT_FORMATS) {
            try {
                return LocalDate.parse(text, format).atStartOfDay();
            } catch (DateTimeParseException ignoredFormat) {
                // Try the next supported date format.
            }
        }
        for (DateTimeFormatter format : INPUT_FORMATS) {
            try {
                return LocalDateTime.parse(text, format);
            } catch (DateTimeParseException ignoredFormat) {
                // Try the next supported format.
            }
        }
        throw new DateTimeParseException("Unsupported date/time", text, 0);
    }

    /**
     * Parses a deadline date or date/time string.
     * A date-only deadline is treated as due at the end of that calendar day.
     *
     * @param value the date or date/time text to parse
     * @return the parsed date/time, with date-only values set to the end of their day
     * @throws DateTimeParseException if the value is blank or uses an unsupported format
     */
    public static LocalDateTime parseDeadline(String value) {
        if (value == null || value.isBlank()) {
            throw new DateTimeParseException("A date/time value is required", "", 0);
        }
        String text = value.trim();
        for (DateTimeFormatter format : DATE_INPUT_FORMATS) {
            try {
                return LocalDate.parse(text, format).atTime(LocalTime.MAX);
            } catch (DateTimeParseException ignoredFormat) {
                // Try the next supported date format.
            }
        }
        return parse(text);
    }

    /** Creates a formatter that rejects invalid dates and times instead of adjusting them. */
    private static DateTimeFormatter strictFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.STRICT);
    }

    /** Formats a date/time for friendly display. */
    public static String format(LocalDateTime value) {
        if (value == null) {
            throw new IllegalArgumentException("A date/time value is required");
        }
        if (value.toLocalTime().equals(LocalTime.MIDNIGHT)
                || value.toLocalTime().equals(LocalTime.MAX)) {
            return value.format(DATE_OUTPUT);
        }
        return value.format(TIME_OUTPUT).replace(" AM", " am").replace(" PM", " pm");
    }
}
