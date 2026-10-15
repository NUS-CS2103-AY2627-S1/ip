package jimbo.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import jimbo.exception.JimboException;

/**
 * Represents a task that starts at a specific date/time and ends at a
 * specific date/time, e.g. "team project meeting /from 2/10/2026 1400
 * /to 2/10/2026 1600". Note that {@code from} and {@code to} are each a
 * full date/time on their own; the date is not shared or inferred between
 * them, so it must be repeated even when both fall on the same day.
 */
public class Event extends Task {
    /**
     * Format accepted for the "from"/"to" date/time when typed by the user
     * in a command, e.g. "2/12/2026 1800".
     */
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    /**
     * Format used to display the "from"/"to" date/time back to the user,
     * e.g. "Dec 02 2026, 6:00PM". Pinned to {@link Locale#US} so the month
     * name and AM/PM marker render the same way regardless of the machine's
     * default locale (some locales render the AM/PM marker in lowercase).
     */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma", Locale.US);

    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * @throws JimboException if {@code from} or {@code to} does not match
     *                        the expected "d/M/yyyy HHmm" format, e.g.
     *                        "2/12/2026 1800".
     */
    public Event(String description, String from, String to) throws JimboException {
        super(description);
        setFrom(from);
        setTo(to);
    }

    /**
     * Constructs an Event from already-parsed {@link LocalDateTime} values,
     * e.g. when reconstructing a task from the save file.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Updates this event's "from" (start) date/time, e.g. in response to an
     * "update" command.
     *
     * @throws JimboException if {@code from} does not match the expected
     *                        "d/M/yyyy HHmm" format, e.g. "2/12/2026 1800".
     */
    public void setFrom(String from) throws JimboException {
        try {
            this.from = LocalDateTime.parse(from, INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new JimboException("\"" + from
                    + "\" isn't a start date I understand — use d/M/yyyy HHmm, e.g. 2/12/2026 1800.");
        }
    }

    /**
     * Updates this event's "to" (end) date/time, e.g. in response to an
     * "update" command.
     *
     * @throws JimboException if {@code to} does not match the expected
     *                        "d/M/yyyy HHmm" format, e.g. "2/12/2026 1800".
     */
    public void setTo(String to) throws JimboException {
        try {
            this.to = LocalDateTime.parse(to, INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new JimboException("\"" + to
                    + "\" isn't an end date I understand — use d/M/yyyy HHmm, e.g. 2/12/2026 1800.");
        }
    }

    @Override
    public String getTypeIcon() {
        return "E";
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    public String toSaveFormat() {
        return "E | " + (status == DoneStatus.DONE ? 1 : 0) + " | " + description + " | "
                + from.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " | "
                + to.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
