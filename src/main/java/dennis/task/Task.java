package dennis.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import dennis.DennisException;

/**
 * Base type for everything the user can put on the list: a description plus a
 * done/not-done flag.
 *
 * <p>Concrete subtypes ({@link Todo}, {@link Deadline}, {@link Event}) add
 * their own fields and decide how the task is shown and saved. {@code Task}
 * itself provides the shared pieces: completion tracking, the plain-text
 * form, and the static helpers for parsing and formatting dates and for
 * guarding the save-file separator.</p>
 */
public abstract class Task {
    /** Field separator used in the save-file format (see {@link #toFileFormat()}). */
    public static final String SAVE_SEPARATOR = " | ";

    /**
     * Format used when showing a date to the user, e.g. {@code Dec 01 2019}.
     * {@link Locale#ENGLISH} is pinned so the month abbreviation is always the
     * English one regardless of the machine's default locale.
     */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** The task text as shown to the user. */
    protected final String description;

    /** Whether the task has been completed. */
    private boolean isDone;

    /**
     * Creates a not-done task with the given description.
     *
     * @param description the task text; subclasses validate it (non-blank, no
     *                    {@code '|'}) before passing it here
     */
    protected Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not completed. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the symbol shown in the task's box.
     *
     * @return {@code "X"} if the task is done, a single space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the digit used in the save file to record completion status.
     *
     * @return "1" if the task is done, "0" otherwise
     */
    protected String getStatusNumber() {
        return isDone ? "1" : "0";
    }

    /**
     * Encodes this task as a single line for the save file. Each subclass
     * supplies its own type tag ("T", "D", "E") and fields, separated by
     * {@link #SAVE_SEPARATOR}. Example: {@code T | 1 | read book}.
     *
     * @return the save-file representation of this task
     */
    public abstract String toFileFormat();

    /**
     * Parses user-supplied text into a {@link LocalDate}. The only accepted
     * form is ISO {@code yyyy-MM-dd} (e.g. {@code 2019-12-01}), which is also
     * exactly what {@link LocalDate#toString()} writes to the save file, so a
     * date can be read back with this same method.
     *
     * <p>Zero-padding is required: {@code 2019-1-5} is rejected.</p>
     *
     * @param raw       the trimmed text for one date field
     * @param fieldName human-readable field name, used in the error message
     * @return the parsed date
     * @throws DennisException if {@code raw} is blank or not a valid
     *                         {@code yyyy-MM-dd} date
     */
    public static LocalDate parseDate(String raw, String fieldName)
            throws DennisException {
        if (raw.isBlank()) {
            throw new DennisException(fieldName + " cannot be empty.");
        }

        try {
            return LocalDate.parse(raw.trim());
        } catch (DateTimeParseException e) {
            throw new DennisException(fieldName
                    + " must be a date in yyyy-MM-dd form, e.g. 2019-12-01.");
        }
    }

    /**
     * Renders a date for display to the user using {@link #DISPLAY_FORMAT}.
     *
     * @param date the date to format
     * @return the date as {@code MMM dd yyyy}, e.g. {@code Dec 01 2019}
     */
    public static String formatDate(LocalDate date) {
        return date.format(DISPLAY_FORMAT);
    }

    /**
     * Reports whether this task falls on the given date. The base
     * implementation returns {@code false}; task types that carry a date
     * (deadline, event) override this.
     *
     * @param date the date to test against
     * @return {@code true} if the task occurs on {@code date}
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Reports whether this task's description contains the given keyword.
     * The match is case-sensitive and considers the description text only,
     * not the type tag, completion status, or any dates.
     *
     * @param keyword the text to search for
     * @return {@code true} if the description contains {@code keyword}
     */
    public boolean matches(String keyword) {
        return description.contains(keyword);
    }

    /**
     * Rejects field text that would clash with the save-file format. Because
     * fields are separated by "{@code  | }", a "{@code |}" inside a description
     * or date/time would make the saved line impossible to read back
     * unambiguously. Forbidding the character keeps the format simple; the
     * alternative would be an escaping scheme in {@code toFileFormat} and the
     * loader.
     *
     * @param value     user-supplied text for one field
     * @param fieldName human-readable field name, used in the error message
     * @return {@code value} unchanged when it is safe to store
     * @throws DennisException if {@code value} contains a "{@code |}" character
     */
    protected static String rejectSeparator(String value, String fieldName)
            throws DennisException {
        if (value.contains("|")) {
            throw new DennisException(
                    fieldName + " cannot contain the '|' character.");
        }

        return value;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
