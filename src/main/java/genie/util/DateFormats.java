package genie.util;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/** Provides the date and time formats shared across the application. */
public final class DateFormats {
    /** Format used to parse task dates and times. */
    public static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);
    /** Format used to display task dates and times. */
    public static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy, h:mm a");

    private DateFormats() {
    }
}
