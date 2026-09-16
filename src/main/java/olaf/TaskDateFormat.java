package olaf;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Parses and formats dates used by deadline and event tasks.
 */
final class TaskDateFormat {
    private static final Pattern INPUT_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private TaskDateFormat() {
    }

    /**
     * Parses a date written in the required ISO format.
     *
     * @param dateText date text in yyyy-MM-dd format.
     * @return parsed date
     * @throws DateTimeParseException if the text has the wrong format or is not a valid date
     */
    static LocalDate parse(String dateText) {
        if (!hasInputFormat(dateText)) {
            throw new DateTimeParseException("Date must use yyyy-MM-dd", dateText, 0);
        }
        return LocalDate.parse(dateText, INPUT_FORMATTER);
    }

    /** Returns whether the text has the required shape, independently of calendar validity. */
    static boolean hasInputFormat(String dateText) {
        return INPUT_PATTERN.matcher(dateText).matches();
    }

    /**
     * Formats a date for display to the user.
     *
     * @param date date to format.
     * @return date formatted as MMM dd yyyy in English
     */
    static String format(LocalDate date) {
        return date.format(DISPLAY_FORMATTER);
    }
}
