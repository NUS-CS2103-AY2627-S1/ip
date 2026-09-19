package yuno.util;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Provides the date and date-time formats used for user input, display, and storage.
 */
public final class DateTimeFormats {
    /** Parses date-time values entered by users. */
    public static final DateTimeFormatter DATE_TIME_INPUT_FORMATTER = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HHmm", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);

    /** Parses date-only values entered by users. */
    public static final DateTimeFormatter DATE_INPUT_FORMATTER = DateTimeFormatter
            .ofPattern("uuuu-MM-dd", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);

    /** Formats date-time values displayed to users. */
    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter
            .ofPattern("MMM dd yyyy, hh:mm a", Locale.ENGLISH);

    /** Parses and formats date-time values stored in the task data file. */
    public static final DateTimeFormatter STORAGE_FORMATTER = DateTimeFormatter
            .ofPattern("MMM dd uuuu, hh:mm a", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Prevents the utility class from being instantiated.
     */
    private DateTimeFormats() {
    }
}
