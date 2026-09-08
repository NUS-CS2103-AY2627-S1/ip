package crack.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import crack.CrackException;

/**
 * A date the user typed, along with a time of day if they gave one.
 * Accepts either slashes or dashes, and either day-first or year-first order.
 */
public class TaskDate {
    /** Date-and-time shapes we accept from the user, e.g. 2/12/2020 1500. */
    private static final DateTimeFormatter[] DATE_TIME_FORMATS = {
        strictFormat("uuuu-MM-dd HHmm"), strictFormat("d-M-uuuu HHmm"),
    };

    /** Date-only shapes we accept, e.g. 2019-10-15. */
    private static final DateTimeFormatter[] DATE_FORMATS = {
        strictFormat("uuuu-MM-dd"), strictFormat("d-M-uuuu"),
    };

    private static final DateTimeFormatter SAVE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter DATE_DISPLAY = DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final DateTimeFormatter DATE_TIME_DISPLAY = DateTimeFormatter.ofPattern("MMM d yyyy, h:mm a");

    private final LocalDateTime at;
    private final boolean hasTime;

    private TaskDate(LocalDateTime at, boolean hasTime) {
        assert at != null : "a TaskDate always wraps a date that parsed cleanly";
        this.at = at;
        this.hasTime = hasTime;
    }

    /**
     * Reads a date written in any of the formats we accept.
     * Slashes and dashes mean the same thing, and the time of day is optional.
     *
     * @param input The date as the user typed it.
     * @return The date, remembering whether a time came with it.
     * @throws CrackException If the text does not match any format we know.
     */
    public static TaskDate parse(String input) throws CrackException {
        // Slashes and dashes are the same to us, so we only match against dashes below.
        String cleaned = input.replace('/', '-');
        for (DateTimeFormatter format : DATE_TIME_FORMATS) {
            try {
                return new TaskDate(LocalDateTime.parse(cleaned, format), true);
            } catch (DateTimeParseException e) {
                continue;
            }
        }
        for (DateTimeFormatter format : DATE_FORMATS) {
            try {
                return new TaskDate(LocalDate.parse(cleaned, format).atStartOfDay(), false);
            } catch (DateTimeParseException e) {
                continue;
            }
        }
        throw new CrackException("'" + input + "' ain't a real date gng. Try 2/12/2020 1500 or 2019-10-15.");
    }

    /**
     * Builds a formatter that rejects days that do not exist rather than
     * nudging them to the nearest one that does.
     * Strict resolution needs the proleptic year 'uuuu' in place of 'yyyy',
     * which carries no era of its own.
     *
     * @param pattern The date pattern to read.
     * @return A formatter that will not quietly move the date.
     */
    private static DateTimeFormatter strictFormat(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Returns whether this lands strictly later than another date.
     *
     * @param other The date being compared against.
     * @return True if this one comes after it.
     */
    public boolean isAfter(TaskDate other) {
        return at.isAfter(other.at);
    }

    /**
     * Returns the day this lands on, ignoring any time of day.
     */
    public LocalDate toLocalDate() {
        return at.toLocalDate();
    }

    /**
     * Returns just the day, written out for display and never showing a time.
     */
    public String formatDay() {
        return at.format(DATE_DISPLAY);
    }

    /**
     * Returns this date written the way the save file stores it.
     * The time is included only when the user gave one.
     */
    public String toSaveFormat() {
        return hasTime ? at.format(SAVE_FORMAT) : at.toLocalDate().toString();
    }

    @Override
    public String toString() {
        return at.format(hasTime ? DATE_TIME_DISPLAY : DATE_DISPLAY);
    }
}
