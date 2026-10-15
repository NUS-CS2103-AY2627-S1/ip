package waddles;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents an event task that occurs over a time span, from a start
 * point to an end point. {@link Parser} rejects any /from or /to that
 * isn't valid yyyy-mm-dd, optionally followed by an HHmm time, before an
 * Event is ever constructed from user input. fromDate/toDate capture just
 * the date portion (recurrence and schedule matching are date-only); when
 * /from or /to includes a time, they stay null until — and unless — the
 * event becomes recurring, since only recurrence actually needs a
 * structured date to advance by (see {@link #setRecurrence}).
 */
public class Event extends Task {
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");

    // Not final: recur() advances from/to/fromDate/toDate in place to the next occurrence.
    private String from;
    private String to;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Recurrence recurrence = Recurrence.NONE;

    /**
     * Creates an Event task with the given description and time span.
     *
     * @param description Description of the task.
     * @param from Start of the event, ideally in yyyy-mm-dd format.
     * @param to End of the event, ideally in yyyy-mm-dd format.
     */
    public Event(String description, String from, String to) {
        super(description);
        assert from != null && !from.isEmpty() : "Event 'from' text should never be null/empty; "
                + "callers must validate user input before constructing an Event";
        assert to != null && !to.isEmpty() : "Event 'to' text should never be null/empty; "
                + "callers must validate user input before constructing an Event";
        this.from = from;
        this.to = to;
        this.fromDate = parseDate(from);
        this.toDate = parseDate(to);
    }

    /**
     * Attempts to parse the given text as a bare date in yyyy-mm-dd format.
     * Text with a trailing time (e.g. "2026-09-19 1000") is deliberately
     * NOT understood here, so that a plain, non-recurring event with a
     * time keeps displaying that time as-is (see class doc) rather than
     * being silently normalised down to just its date.
     *
     * @param text The text to parse.
     * @return The parsed date, or null if the text is not a bare valid date.
     */
    private static LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses the date portion of the given text, ignoring any trailing
     * " HHmm" time component (e.g. "2026-09-19 1000" is read as the date
     * 2026-09-19). Used only once an event becomes recurring, since
     * recurrence advances by whole days/weeks/months and has no use for
     * time-of-day.
     *
     * @param text The text to parse.
     * @return The parsed date, or null if the date portion isn't valid.
     */
    private static LocalDate parseDateIgnoringTime(String text) {
        int spaceIndex = text.indexOf(' ');
        String datePart = spaceIndex >= 0 ? text.substring(0, spaceIndex) : text;
        return parseDate(datePart);
    }

    public String getFrom() {
        return this.from;
    }

    public String getTo() {
        return this.to;
    }

    /**
     * Returns whether both this event's dates were understood as structured
     * yyyy-mm-dd dates, which is required for recurrence to be able to
     * compute a next occurrence.
     *
     * @return True if both dates were parsed successfully.
     */
    public boolean hasStructuredDate() {
        return fromDate != null && toDate != null;
    }

    /**
     * Sets how often this event repeats. If either date wasn't already
     * understood as a bare yyyy-mm-dd date (e.g. because it included a
     * time, like "2026-09-19 1000"), and a real recurrence is being set,
     * its date portion is parsed now — recurrence needs a structured date
     * to advance by, even though a one-off event with a time doesn't.
     *
     * @param recurrence The recurrence period; use {@link Recurrence#NONE} for a one-off event.
     */
    public void setRecurrence(Recurrence recurrence) {
        this.recurrence = recurrence;
        if (recurrence != Recurrence.NONE) {
            if (fromDate == null) {
                fromDate = parseDateIgnoringTime(from);
            }
            if (toDate == null) {
                toDate = parseDateIgnoringTime(to);
            }
        }
    }

    public Recurrence getRecurrence() {
        return recurrence;
    }

    @Override
    public boolean isRecurring() {
        return recurrence != Recurrence.NONE;
    }

    @Override
    public void recur() {
        if (!isRecurring()) {
            throw new UnsupportedOperationException("This event does not recur");
        }
        fromDate = recurrence.advance(fromDate);
        toDate = recurrence.advance(toDate);
        from = fromDate.toString();
        to = toDate.toString();
        markAsNotDone();
    }

    @Override
    public Task copy() {
        Event copy = new Event(getDescription(), from, to);
        copy.setRecurrence(recurrence);
        if (isDone()) {
            copy.markAsDone();
        }
        return copy;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return fromDate != null && toDate != null && !date.isBefore(fromDate) && !date.isAfter(toDate);
    }

    @Override
    public String toString() {
        String displayFrom = (fromDate != null) ? fromDate.format(OUTPUT_FORMAT) : from;
        String displayTo = (toDate != null) ? toDate.format(OUTPUT_FORMAT) : to;
        String recurrenceSuffix = isRecurring() ? " (every " + recurrence.displayLabel() + ")" : "";
        return "[E]" + super.toString() + " (from: " + displayFrom + " to: " + displayTo + ")" + recurrenceSuffix;
    }
}