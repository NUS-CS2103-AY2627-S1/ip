package lion;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a task that must be completed by a specific date and time.
 */
public class Deadline extends Task {
    // Locale.ENGLISH is pinned explicitly, rather than left to the JVM's default
    // locale, so the save file and the displayed date/time stay identical (numeric
    // fields only, but consistent regardless) no matter what OS language the app runs
    // under; DISPLAY_FORMAT below needs this even more, since it renders text
    // (month name, AM/PM) that would otherwise vary by locale.
    private static final DateTimeFormatter STORAGE_FORMAT =
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm", Locale.ENGLISH);

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy h:mm a", Locale.ENGLISH);

    // Used only by validateByText()'s strict check, not by the lenient constructor
    // below: DateTimeFormatter's default (SMART) resolution silently clamps an
    // out-of-range day like 30 February to the nearest valid day (28 February) instead
    // of rejecting it, so a manual parse plus LocalDateTime.of() — which does reject it
    // — is needed to actually catch non-existent dates.
    private static final Pattern STRICT_DATE_TIME_PATTERN =
            Pattern.compile("(\\d{1,2})/(\\d{1,2})/(\\d{4})\\s+(\\d{2})(\\d{2})");

    /** User-provided task deadline. */
    protected LocalDateTime by;

    /**
     * Creates a deadline from its description and date-time text.
     *
     * @param description description of the task.
     * @param byText deadline formatted as {@code d/M/yyyy HHmm}.
     */
    public Deadline(String description, String byText) {
        super(description);
        assert byText != null : "deadline date-time text must not be null";

        try {
            this.by = LocalDateTime.parse(byText.trim(), STORAGE_FORMAT);
        } catch (DateTimeParseException e) {
            // Fall back to "now" instead of propagating the exception: this
            // constructor is also called by Storage.decode() while loading the
            // save file, and rejecting one unparseable line there would stop
            // the whole task list from loading. Losing the original date this
            // way is an accepted trade-off for that resilience.
            this.by = LocalDateTime.now();
        }

        // Both branches above assign `by`, so callers of getByForStorage()/
        // toString() can always rely on it being set.
        assert by != null : "by must be set by either the parsed value or the fallback";
    }

    /**
     * Returns the one-letter icon used to identify deadline tasks.
     *
     * @return {@code D}.
     */
    @Override
    public String getTypeIcon() {
        return "D";
    }

    /**
     * Validates deadline date-time text without constructing a Deadline, so newly
     * typed deadlines can be rejected with a clear message instead of silently falling
     * back to "now" the way the lenient constructor above does for save-file resilience.
     *
     * @param byText candidate deadline text.
     * @throws LionException if the text does not match {@code d/M/yyyy HHmm}, or names a
     *     date that does not exist (e.g. 30/2/2019).
     */
    public static void validateByText(String byText) throws LionException {
        if (parseStrict(byText) == null) {
            throw new LionException("Lion can't make sense of that date — use d/M/yyyy HHmm "
                    + "(e.g. 2/12/2019 1800), and check that the date actually exists.");
        }
    }

    /**
     * Strictly parses {@code d/M/yyyy HHmm} text, rejecting both malformed text and
     * dates that do not exist on the calendar (e.g. 30 February).
     *
     * @param text candidate date-time text.
     * @return the parsed date-time, or {@code null} if it is malformed or names a
     *     date/time that does not exist.
     */
    static LocalDateTime parseStrict(String text) {
        Matcher matcher = STRICT_DATE_TIME_PATTERN.matcher(text.trim());
        if (!matcher.matches()) {
            return null;
        }

        try {
            return LocalDateTime.of(
                    Integer.parseInt(matcher.group(3)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(4)),
                    Integer.parseInt(matcher.group(5)));
        } catch (DateTimeException e) {
            return null;
        }
    }

    /**
     * Returns whether another task is a deadline with the same description and due date.
     *
     * @param other task to compare against.
     * @return true if the two deadlines share the same description and due date.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other) && by.equals(((Deadline) other).by);
    }

    /**
     * Returns the deadline in the stable format used in the save file.
     *
     * @return deadline formatted as {@code d/M/yyyy HHmm}.
     */
    public String getByForStorage() {
        return by.format(STORAGE_FORMAT);
    }

    /**
     * Returns a display-friendly representation of this deadline.
     *
     * @return deadline status, description, and formatted due date.
     */
    @Override
    public String toString() {
        String formattedDateTime = by.format(DISPLAY_FORMAT);
        return super.toString() + " (by: " + formattedDateTime + ")";
    }
}
