
package ip.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a parsed date, optionally including a time. */
public class ParsedDateTime {

    private static final DateTimeFormatter STORAGE_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter STORAGE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma", Locale.US);
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.US);

    private final LocalDateTime dateTime;
    private final boolean hasTime;

    /** Creates a parsed date-time value. */
    public ParsedDateTime(LocalDateTime dateTime, boolean hasTime) {
        this.dateTime = dateTime;
        this.hasTime = hasTime;
    }

    /** Returns the storage-friendly date or date-time format. */
    public String toFileFormat() {
        return hasTime
                ? dateTime.format(STORAGE_DATE_TIME_FORMATTER)
                : dateTime.format(STORAGE_DATE_FORMATTER);
    }

    /** Returns the underlying date and time for chronological validation. */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    /** Returns the user-facing date or date-time format. */
    public String toString() {
        return hasTime
                ? dateTime.format(DISPLAY_DATE_TIME_FORMATTER)
                : dateTime.format(DISPLAY_DATE_FORMATTER);
    }
}
