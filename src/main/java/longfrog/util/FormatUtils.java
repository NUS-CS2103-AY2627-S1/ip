package longfrog.util;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.Map;

/** Provides shared date and time formats for parsing, persistence, and display. */
public class FormatUtils {
    /** Format used for command input and persistent task data. */
    public static final DateTimeFormatter INPUT_SAVE_FORMAT = DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);

    /** Format used for date-only commands and output. */
    public static final DateTimeFormatter DATE_ONLY_FORMAT = DateTimeFormatter.ofPattern("d/M/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    /** Locale-independent format used for human-readable date-time output. */
    public static final DateTimeFormatter PRINT_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("MMM dd yyyy, h:mm ")
            .appendText(ChronoField.AMPM_OF_DAY, Map.of(0L, "am", 1L, "pm"))
            .toFormatter(Locale.ENGLISH);
}
