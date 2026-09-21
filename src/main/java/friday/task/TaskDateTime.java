package friday.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Parses supported task dates strictly and formats them consistently in English.
 */
public final class TaskDateTime {
    private static final DateTimeFormatter FORMAT_INPUT_WITH_TIME = createFormatter("uuuu-MM-dd HH:mm");
    private static final DateTimeFormatter FORMAT_DAY_FIRST_WITH_TIME = createFormatter("d/M/uuuu HHmm");
    private static final DateTimeFormatter FORMAT_STORAGE_WITH_TIME = createFormatter("uuuu-MM-dd'T'HH:mm");
    private static final DateTimeFormatter FORMAT_DISPLAY_DATE = createFormatter("MMM dd uuuu");
    private static final DateTimeFormatter FORMAT_DISPLAY_TIME = createFormatter("HH:mm");

    private TaskDateTime() {
    }

    /**
     * Parses an ISO date (at midnight), an ISO date with HH:mm, or a day-first date with HHmm.
     * Also accepts the ISO date-time representation written to the save file.
     *
     * @throws IllegalArgumentException if the input is not a supported, valid date and time.
     */
    public static LocalDateTime parse(String text) {
        try {
            if (text.contains("/")) {
                return LocalDateTime.parse(text, FORMAT_DAY_FIRST_WITH_TIME);
            } else if (text.contains("T")) {
                return LocalDateTime.parse(text, FORMAT_STORAGE_WITH_TIME);
            } else if (text.contains(" ")) {
                return LocalDateTime.parse(text, FORMAT_INPUT_WITH_TIME);
            }
            return LocalDate.parse(text).atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date/time. Use yyyy-MM-dd, yyyy-MM-dd HH:mm, "
                    + "or d/M/yyyy HHmm (e.g., 2/12/2019 1800).", e);
        }
    }

    /**
     * Displays midnight as a date alone and other times with a 24-hour HH:mm suffix.
     */
    public static String format(LocalDateTime dateTime) {
        String date = dateTime.format(FORMAT_DISPLAY_DATE);
        return dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)
                ? date
                : date + ", " + dateTime.format(FORMAT_DISPLAY_TIME);
    }

    /**
     * Creates a formatter that rejects impossible dates instead of adjusting them to a valid day.
     */
    private static DateTimeFormatter createFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT);
    }
}
