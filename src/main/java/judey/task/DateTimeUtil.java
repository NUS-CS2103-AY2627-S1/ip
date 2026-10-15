package judey.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import judey.exception.JudeyException;

/**
 * Provides shared date/time parsing and formatting logic used by {@link Deadline} and {@link Event}.
 * Extracted here to avoid duplicating the same parsing rules and formats in both task types.
 */
public final class DateTimeUtil {
    private static final String DATE_TIME_PATTERN = "\\d{1,2}/\\d{1,2}/\\d{4} \\d{4}";
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");

    private DateTimeUtil() {
        // Prevents instantiation; this class only exposes static utility methods.
    }

    public static LocalDateTime parse(String text) throws JudeyException {
        try {
            return LocalDateTime.parse(text);
        } catch (DateTimeParseException isoParseFailed) {
            try {
                return LocalDateTime.parse(text, INPUT_FORMAT);
            } catch (DateTimeParseException userFormatParseFailed) {
                if (text.matches(DATE_TIME_PATTERN)) {
                    throw new JudeyException("The date/time you entered is invalid. "
                            + "Please use a real date in the format d/M/yyyy HHmm.");
                }
                throw new JudeyException("Please use date format: d/M/yyyy HHmm (e.g., 2/12/2019 1800)");
            }
        }
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime.format(OUTPUT_FORMAT);
    }
}
