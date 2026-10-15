package jeryl.task;

import java.time.format.DateTimeFormatter;

/**
 * Shared date formatting used across task types that carry a date
 * (Deadline, Event), so the display format is defined once instead of
 * being duplicated in each class.
 */
final class DateFormats {
    /** The format dates are shown in when a task is printed for the user, e.g. "Dec 25 2026". */
    static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private DateFormats() {
    }
}
