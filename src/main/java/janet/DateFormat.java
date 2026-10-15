package janet;

import java.time.format.DateTimeFormatter;

/**
 * Provides date and time formatting used by Janet's task display.
 */
public class DateFormat {

    /**
     * Formats dates as a three-letter month, day, and four-digit year.
     * For example, {@code Jan 5 2026}.
     */
    public static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy");
}
