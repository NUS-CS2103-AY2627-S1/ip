package botavius.tasklist;

import botavius.exception.BotaviusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Parses and formats the date/time values used by deadline and event tasks. */
final class TaskDateTime {
    /** Accepted input and storage format. */
    private static final DateTimeFormatter STORAGE_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("dd-MM-uuuu HH:mm")
            .toFormatter(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    /** User-facing display format. */
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter
            .ofPattern("dd-MMM-yy hh:mm a", Locale.ENGLISH);

    private TaskDateTime() {
    }

    /**
     * Parses a date with an optional 24-hour time.
     *
     * @param value input in {@code dd-MM-yyyy} or {@code dd-MM-yyyy HH:mm}
     * @param itemName name used in validation errors
     * @return the parsed date and time
     * @throws BotaviusException if the value is missing or malformed
     */
    static LocalDateTime parse(String value, String itemName) {
        if (value == null || value.isBlank()) {
            throw new BotaviusException(itemName + " not provided.");
        }
        String normalized = value.strip();
        if (normalized.matches("\\d{2}-\\d{2}-\\d{4}")) {
            normalized += " 00:00";
        }
        try {
            return LocalDateTime.parse(normalized, STORAGE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new BotaviusException(itemName
                    + " must use dd-MM-yyyy with an optional 24-hour time (HH:mm).");
        }
    }

    /** @return the regular storage representation */
    static String toStorageString(LocalDateTime value) {
        return STORAGE_FORMAT.format(value);
    }

    /** @return the readable display representation */
    static String toDisplayString(LocalDateTime value) {
        return DISPLAY_FORMAT.format(value).replace("AM", "am").replace("PM", "pm");
    }
}
