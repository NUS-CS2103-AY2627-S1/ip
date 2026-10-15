package greenchonk.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

/**
 * Parses and formats calendar dates used by tasks.
 */
public final class TaskDate {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DAY_MONTH_YEAR_FORMAT =
            strictFormatter("d/M/uuuu", false);
    private static final DateTimeFormatter TEXT_MONTH_FORMAT =
            strictFormatter("d MMM uuuu", true);
    private static final List<DateTimeFormatter> INPUT_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DAY_MONTH_YEAR_FORMAT,
            TEXT_MONTH_FORMAT);

    private TaskDate() {
    }

    /**
     * Parses a date in one of Green Chonk's supported input formats.
     *
     * @param dateText the complete date text.
     * @return the parsed calendar date.
     * @throws DateTimeParseException if the text does not represent a valid supported date.
     */
    public static LocalDate parse(String dateText) {
        for (DateTimeFormatter inputFormat : INPUT_FORMATS) {
            try {
                return LocalDate.parse(dateText, inputFormat);
            } catch (DateTimeParseException exception) {
                // Continue with the next explicitly supported format.
            }
        }
        throw new DateTimeParseException("Unsupported or invalid task date", dateText, 0);
    }

    /**
     * Formats a date for display to the user.
     *
     * @param date the date to format.
     * @return the date in a friendly English format.
     */
    public static String format(LocalDate date) {
        return date.format(DISPLAY_FORMAT);
    }

    /**
     * Creates a strict formatter for a supported input pattern.
     *
     * @param pattern the date pattern.
     * @param isCaseInsensitive whether letters can use any capitalization.
     * @return the configured formatter.
     */
    private static DateTimeFormatter strictFormatter(String pattern, boolean isCaseInsensitive) {
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        if (isCaseInsensitive) {
            builder.parseCaseInsensitive();
        }
        return builder.appendPattern(pattern)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }
}
