package yachiyo.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Formats task date-times consistently for display and storage.
 */
final class TaskDateTimeFormatter {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm");

    private TaskDateTimeFormatter() {
    }

    static String formatForDisplay(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_FORMATTER);
    }

    static String formatForStorage(LocalDateTime dateTime) {
        return dateTime.format(STORAGE_FORMATTER);
    }
}
