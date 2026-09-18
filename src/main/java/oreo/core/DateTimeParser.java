package oreo.core;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/** Parses the calendar date formats understood by Oreo and formats them for display. */
public final class DateTimeParser {
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy h:mma", Locale.ENGLISH);
    private static final DateTimeFormatter SLASH_DATE_TIME =
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    private DateTimeParser() {
    }

    /** Parses an ISO calendar date such as {@code 2019-10-15}. */
    public static LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Use a date in yyyy-MM-dd format.");
        }
    }

    /** Returns a parsed value, or null when the input is intentionally free-form legacy text. */
    public static LocalDateTime parse(String text) {
        try {
            if (text.matches("\\d{1,2}/\\d{1,2}/\\d{4} \\d{4}")) {
                return LocalDateTime.parse(text, SLASH_DATE_TIME);
            }
            if (text.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date or time: " + text, e);
        }
        return null;
    }

    /** Formats parsed values while leaving old free-form values unchanged. */
    public static String format(LocalDateTime value, String original) {
        if (value == null) {
            return original;
        }
        return value.toLocalTime().equals(LocalTime.MIDNIGHT)
                ? value.format(DISPLAY_DATE)
                : value.format(DISPLAY_DATE_TIME);
    }
}
