package kevin.task;

import kevin.KevinException;

import java.time.LocalDateTime;

/**
 * Event Class is a Task with a from and to date.
 */
public class Event extends Task {
    private LocalDateTime from;
    private LocalDateTime to;

    /**
     * Creates new Event.
     * Checks that to DateTime is later than from.
     *
     * @param description Description of Event.
     * @param isDone Whether Event is done.
     * @param from From date of Event.
     * @param to To date of Event.
     * @throws KevinException If to is earlier than from.
     */
    public Event(String description, boolean isDone, LocalDateTime from, LocalDateTime to) throws KevinException {
        checkFromLaterThanTo(from, to);

        super(description, isDone);
        this.from = from;
        this.to = to;
    }

    /**
     * Creates new Event with default isDone as false.
     * Checks that DateTimes are later than now.

     * @throws KevinException If DateTimes are earlier than now, or to is earlier than from.
     */
    public Event(String description, LocalDateTime from, LocalDateTime end) throws KevinException {
        checkDateTimeLaterThanNow(from);
        checkDateTimeLaterThanNow(end);

        this(description, false, from, end);
    }

    /**
     * Snoozes from and/or to dates to a later DateTime.
     *
     * @param newFrom New delayed from DateTime.
     * @param newTo New delayed to DateTime.
     * @throws KevinException If new from/to is later than now, or to is earlier than from.
     */
    public void snooze(LocalDateTime newFrom, LocalDateTime newTo) throws KevinException {
        checkDateTimeLaterThanNow(newFrom);
        checkDateTimeLaterThanNow(newTo);
        checkFromLaterThanTo(newFrom, newTo);

        this.from = newFrom;
        this.to = newTo;
    }

    /**
     * Checks that from DateTime is later than to DateTime.
     *
     * @param from From DateTime.
     * @param to To DateTime.
     * @throws KevinException If to is earlier than from.
     */
    public static void checkFromLaterThanTo(LocalDateTime from, LocalDateTime to) throws KevinException {
        if (to.isBefore(from)) {
            throw new KevinException("To DateTime cannot be earlier than from DateTime.");
        }
    }

    /**
     * Format Event into String for saving into tasks.txt.
     */
    @Override
    public String formatSaveString() {
        return "E | " + super.formatSaveString() + " | " + formatDateTime(from)
                + " | " + formatDateTime(to);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + formatDateTime(from)
                + " to: " + formatDateTime(to) + ")";
    }
}
