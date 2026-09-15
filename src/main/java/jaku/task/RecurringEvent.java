package jaku.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents an event whose start and end both recur at a fixed interval. */
public class RecurringEvent extends Task {
    /** Formatter used for recurring event date-times shown in Jaku's user interface. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu HH:mm", Locale.ENGLISH);

    /** Interval between occurrences. */
    private final Recurrence recurrence;

    /** Start of the occurrence currently tracked by this task. */
    private LocalDateTime from;

    /** End of the occurrence currently tracked by this task. */
    private LocalDateTime to;

    /**
     * Creates an incomplete recurring event.
     *
     * @param description text describing the event
     * @param from start of the first occurrence
     * @param to end of the first occurrence
     * @param recurrence interval between occurrences
     */
    public RecurringEvent(String description, LocalDateTime from, LocalDateTime to, Recurrence recurrence) {
        super(description);
        this.from = from;
        this.to = to;
        this.recurrence = recurrence;
    }

    /**
     * Returns the interval between occurrences.
     *
     * @return this event's recurrence interval
     */
    public Recurrence getRecurrence() {
        return recurrence;
    }

    /**
     * Returns the start of the current occurrence.
     *
     * @return current occurrence start date-time
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the end of the current occurrence.
     *
     * @return current occurrence end date-time
     */
    public LocalDateTime getTo() {
        return to;
    }

    /** Advances this event to its following occurrence. */
    public void advanceOccurrence() {
        from = recurrence.advance(from);
        to = recurrence.advance(to);
    }

    /** Restores this event to its previous occurrence. */
    public void reverseOccurrence() {
        from = recurrence.reverse(from);
        to = recurrence.reverse(to);
    }

    /** Returns this recurring event with its schedule details. */
    @Override
    public String toString() {
        return "[R][" + getStatusIcon() + "] " + getDescription() + " (every: " + recurrence.getLabel()
                + ", from: " + DISPLAY_DATE_TIME_FORMAT.format(from)
                + " to: " + DISPLAY_DATE_TIME_FORMAT.format(to) + ")";
    }
}
