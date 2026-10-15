package jimbo.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import jimbo.exception.JimboException;

/**
 * Represents a task that needs to be done before a specific date/time,
 * e.g. "submit report by 11/10/2026 5pm".
 */
public class Deadline extends Task {
    /**
     * Format accepted for the "by" date/time when typed by the user in a
     * command, e.g. "2/12/2026 1800".
     */
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    /**
     * Format used to display the "by" date/time back to the user,
     * e.g. "Dec 02 2026, 6:00PM". Pinned to {@link Locale#US} so the month
     * name and AM/PM marker render the same way regardless of the machine's
     * default locale (some locales render the AM/PM marker in lowercase).
     */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma", Locale.US);

    protected LocalDateTime by;

    /**
     * @throws JimboException if {@code by} does not match the expected
     *                        "d/M/yyyy HHmm" format, e.g. "2/12/2026 1800".
     */
    public Deadline(String description, String by) throws JimboException {
        super(description);
        setBy(by);
    }

    /**
     * Constructs a Deadline from an already-parsed {@link LocalDateTime},
     * e.g. when reconstructing a task from the save file.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Updates this deadline's "by" date/time, e.g. in response to an
     * "update" command.
     *
     * @throws JimboException if {@code by} does not match the expected
     *                        "d/M/yyyy HHmm" format, e.g. "2/12/2026 1800".
     */
    public void setBy(String by) throws JimboException {
        try {
            this.by = LocalDateTime.parse(by, INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new JimboException("\"" + by
                    + "\" isn't a date I understand — use d/M/yyyy HHmm, e.g. 2/12/2026 1800.");
        }
    }

    @Override
    public String getTypeIcon() {
        return "D";
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    public String toSaveFormat() {
        return "D | " + (status == DoneStatus.DONE ? 1 : 0) + " | " + description + " | "
                + by.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
