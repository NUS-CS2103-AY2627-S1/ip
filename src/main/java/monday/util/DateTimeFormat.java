package monday.util;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Defines the date and time formats accepted and displayed by Monday.
 */
public enum DateTimeFormat {
    INPUT_DATE("dd/MM/uuuu"),
    INPUT_TIME("HHmm"),
    DISPLAY_DATE("dd MMM uuuu"),
    DISPLAY_TIME("HHmm");

    private final DateTimeFormatter formatter;

    /**
     * Creates a formatter for a specific pattern.
     */
    DateTimeFormat(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Returns the formatter represented by this enum constant.
     *
     * @return the date or time formatter.
     */
    public DateTimeFormatter getFormatter() {
        return this.formatter;
    }
}
