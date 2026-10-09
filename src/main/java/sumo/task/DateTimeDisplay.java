package sumo.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Formats typed date and time values for Sumo's user-facing output.
 */
public final class DateTimeDisplay {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy h:mm a", Locale.ENGLISH);

    private DateTimeDisplay() {
        // Utility class.
    }

    /**
     * Formats a date and optionally its time.
     *
     * @param value the value to format.
     * @param hasTime whether the value includes a time.
     * @return the formatted value.
     */
    public static String format(LocalDateTime value, boolean hasTime) {
        return value.format(hasTime ? DATE_TIME_FORMATTER : DATE_FORMATTER);
    }
}
