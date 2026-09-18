package zhangwei.task;

import java.time.LocalDate;
import java.util.Optional;

/**
 * A task that runs between a start and an end date,
 * e.g. "project meeting (from: Dec 3 2019 to: Dec 4 2019)".
 */
public class Event extends Task {

    /** The date this event starts. */
    private final LocalDate from;

    /** The date this event ends. */
    private final LocalDate to;

    /**
     * Creates an event running from the given start to the given end.
     *
     * @param description what is happening, e.g. "project meeting".
     * @param from the date the event starts.
     * @param to the date the event ends.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start date of this event.
     *
     * @return the date the event starts.
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns the end date of this event.
     *
     * @return the date the event ends.
     */
    public LocalDate getTo() {
        return to;
    }

    /**
     * Returns the date this event is anchored to, which is the date it starts.
     * An event is placed by when it begins, the way it would be read off a
     * calendar.
     *
     * @return the start date, never empty.
     */
    @Override
    public Optional<LocalDate> getDate() {
        return Optional.of(from);
    }

    /**
     * Returns this event as it appears to the user, with the dates written in
     * a readable form rather than the ISO form they were typed in.
     *
     * @return this event as one line,
     *     e.g. "[E][ ] project meeting (from: Dec 3 2019 to: Dec 4 2019)".
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + DateDisplay.format(from) + " to: "
                + DateDisplay.format(to) + ")";
    }
}
