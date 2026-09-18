package chatbot;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Provides shared date and time formats for the chatbot.
 */
final class DateTimeFormats {
    static final DateTimeFormatter STORAGE = new DateTimeFormatterBuilder()
        .appendPattern("uuuu-MM-dd HHmm")
        .toFormatter()
        .withResolverStyle(ResolverStyle.STRICT);

    static final DateTimeFormatter DISPLAY_DATE_TIME =
        DateTimeFormatter.ofPattern("dd MMM yyyy h:mm a", Locale.ENGLISH);

    static final DateTimeFormatter DATE = new DateTimeFormatterBuilder()
        .appendPattern("uuuu-MM-dd")
        .toFormatter()
        .withResolverStyle(ResolverStyle.STRICT);

    static final DateTimeFormatter DISPLAY_DATE =
        DateTimeFormatter.ofPattern("dd MMM yyyy");

    static final String INPUT_FORMAT = "yyyy-MM-dd HHmm";

    private DateTimeFormats() {
        // Prevent instantiation.
    }

}
