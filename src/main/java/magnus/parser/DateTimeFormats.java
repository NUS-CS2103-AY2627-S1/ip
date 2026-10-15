package magnus.parser;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Provides the shared date-time formats used for input, storage, and display.
 */
public final class DateTimeFormats {
    /** Strict date format accepted from user input. */
    public static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter
            .ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    /** Strict date-time format accepted from user input and used in storage. */
    public static final DateTimeFormatter FORMATTER_DATE_TIME = DateTimeFormatter
            .ofPattern("dd/MM/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);

    /** English date-time format used when displaying tasks. */
    public static final DateTimeFormatter FORMATTER_DISPLAY_DATE_TIME = DateTimeFormatter
            .ofPattern("MMM dd, uuuu HH:mm", Locale.ENGLISH);

    private DateTimeFormats() {
    }
}
