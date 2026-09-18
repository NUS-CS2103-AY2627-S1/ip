package zhangwei.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Writes dates the way the user reads them, e.g. "Dec 2 2019".
 *
 * <p>Deadlines and events both show dates, and showing them in two different
 * forms would be a fault the user notices long before the programmer does.
 * Keeping the one format here leaves a single place to change it.
 */
final class DateDisplay {

    /** The form every date takes when the chatbot shows it, e.g. "Dec 2 2019". */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    /** Prevents instantiation: this class is only a home for a static method. */
    private DateDisplay() {
    }

    /**
     * Returns the given date written the way the user reads it.
     *
     * @param date the date to write out.
     * @return the date as text, e.g. "Dec 2 2019".
     */
    static String format(LocalDate date) {
        return date.format(DISPLAY_FORMAT);
    }
}
