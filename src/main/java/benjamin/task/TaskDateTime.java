package benjamin.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import benjamin.BenjaminException;

/**
 * A date, with an optional time of day, attached to a task.
 *
 * <p>Keeping this in one class means the accepted input formats, the display
 * format and the save-file format are defined once instead of being repeated
 * in every task type that carries a date.
 */
public class TaskDateTime {
    /** Human readable list of what the user is allowed to type. */
    public static final String ACCEPTED_FORMATS =
            "yyyy-MM-dd, yyyy-MM-dd HHmm, d/M/yyyy or d/M/yyyy HHmm";

    // Parsing is strict so that a date which does not exist is rejected rather
    // than quietly moved. Under the default lenient style, 2019-02-30 becomes
    // 28 February without a word to the user. Strict resolution needs the
    // proleptic-year symbol uuuu in place of yyyy, which is era dependent.
    private static final DateTimeFormatter[] DATE_TIME_FORMATS = {
        strict("uuuu-MM-dd HHmm"),
        strict("d/M/uuuu HHmm"),
    };

    private static final DateTimeFormatter[] DATE_FORMATS = {
        strict("uuuu-MM-dd"),
        strict("d/M/uuuu"),
    };

    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_DATE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm", Locale.ENGLISH);

    private final LocalDateTime dateTime;
    private final boolean hasTime;

    private TaskDateTime(LocalDateTime dateTime, boolean hasTime) {
        // Only parse() constructs these, and it only does so from a value a
        // formatter already accepted, so null cannot reach here.
        assert dateTime != null : "a TaskDateTime must wrap a parsed date";

        this.dateTime = dateTime;
        this.hasTime = hasTime;
    }

    /**
     * Returns the date described by the given text.
     *
     * @param text date written in any of the accepted formats.
     * @throws BenjaminException if the text matches none of them.
     */
    public static TaskDateTime parse(String text) throws BenjaminException {
        String trimmed = text.trim();

        for (DateTimeFormatter format : DATE_TIME_FORMATS) {
            try {
                return new TaskDateTime(LocalDateTime.parse(trimmed, format), true);
            } catch (DateTimeParseException exception) {
                // This format did not match, so fall through and try the next one.
            }
        }

        for (DateTimeFormatter format : DATE_FORMATS) {
            try {
                return new TaskDateTime(LocalDate.parse(trimmed, format).atStartOfDay(), false);
            } catch (DateTimeParseException exception) {
                // This format did not match, so fall through and try the next one.
            }
        }

        throw new BenjaminException("I could not read the date \"" + trimmed
                + "\". Please use " + ACCEPTED_FORMATS + ".");
    }

    /** Returns a formatter that rejects dates which do not exist on the calendar. */
    private static DateTimeFormatter strict(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /** Returns the calendar date, ignoring any time of day. */
    public LocalDate getDate() {
        return dateTime.toLocalDate();
    }

    /**
     * Returns true if this moment falls strictly after the given one.
     *
     * @param other the moment being compared against.
     */
    public boolean isAfter(TaskDateTime other) {
        return dateTime.isAfter(other.dateTime);
    }

    /** Returns the display form of a plain calendar date, such as Oct 15 2019. */
    public static String formatDate(LocalDate date) {
        return date.format(DISPLAY_DATE);
    }

    /** Returns the form written to the save file, which parse() can read back. */
    public String toStorageString() {
        return hasTime ? dateTime.format(STORAGE_DATE_TIME) : dateTime.format(STORAGE_DATE);
    }

    /**
     * Returns the form shown to the user, such as {@code Oct 15 2019} when no
     * time was given, or {@code Dec 02 2019, 6:00pm} when one was.
     */
    @Override
    public String toString() {
        if (!hasTime) {
            return dateTime.format(DISPLAY_DATE);
        }

        return dateTime.format(DISPLAY_DATE_TIME).replace("AM", "am").replace("PM", "pm");
    }
}
