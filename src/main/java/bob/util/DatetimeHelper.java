package bob.util;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/**
 * Provides date-time formatters used across the Bob application.
 */
public class DatetimeHelper {

    /**
     * Provides a formatter for parsing user input date-time strings ({@code dd/MM/yy HH:mm}).
     */
    public static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/uu HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Provides a formatter for displaying date-time strings to the user
     * ({@code dd MMMM yyyy, HHmm 'hrs'}).
     */
    public static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy, HHmm 'hrs'");

    /**
     * Provides a formatter for the ISO-8601 storage format.
     */
    public static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Prevents instantiation of this utility class.
     */
    private DatetimeHelper() {
    }
}

