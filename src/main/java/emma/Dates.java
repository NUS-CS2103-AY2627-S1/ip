package emma;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Renders dates the way Emma shows them to the user.
 */
public class Dates {

    /** The locale is fixed so the month reads the same wherever Emma is run. */
    private static final DateTimeFormatter DISPLAY =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /**
     * Renders a date with its month in words, as in "Oct 15 2019".
     *
     * @param date the date to render.
     * @return the date as Emma shows it.
     */
    public static String format(LocalDate date) {
        return date.format(DISPLAY);
    }
}
