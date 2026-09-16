package brucli.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import brucli.ui.Messages;

/**
 * Converts date-time values between input, storage, and display formats.
 */
public final class DateTimes {
    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu, h:mm a", Locale.ENGLISH);

    private DateTimes() {
    }

    /**
     * Parses user input such as {@code 2026-08-26 1830}.
     */
    public static LocalDateTime parse(String text) {
        try {
            return LocalDateTime.parse(text, INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(Messages.invalidDateTime());
        }
    }

    /**
     * Returns the ISO-8601 representation used in the save file.
     */
    public static String serialize(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Parses an ISO-8601 date-time read from the save file.
     */
    public static LocalDateTime parseStored(String text) {
        try {
            return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(Messages.invalidStoredDateTime(text));
        }
    }

    /**
     * Returns a human-friendly representation for task listings.
     */
    public static String display(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_FORMAT);
    }
}
