package ted.task;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * A task that runs from one point in time to another,
 * e.g. {@code project meeting (from: 2 Dec 2019, 2:00 PM to: 2 Dec 2019, 4:00 PM)}.
 */
public class Event extends Task {
    /** When the event starts. */
    private final LocalDateTime from;

    /** When the event ends. */
    private final LocalDateTime to;

    /**
     * Creates an event that is not done yet.
     *
     * @param description what the event is.
     * @param from        when the event starts.
     * @param to          when the event ends.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** {@inheritDoc} An event is shown as {@code E}. */
    @Override
    public String getTypeIcon() {
        return "E";
    }

    /**
     * {@inheritDoc} An event must also start and end at the same times, since
     * a weekly meeting held on two different days is two separate events.
     */
    @Override
    public boolean isDuplicateOf(Task other) {
        return super.isDuplicateOf(other) && other instanceof Event event
                && from.equals(event.from) && to.equals(event.to);
    }

    /** {@inheritDoc} An event checks both its start and its end. */
    @Override
    public boolean isAtTimeOfDay(LocalTime time) {
        return from.toLocalTime().equals(time) || to.toLocalTime().equals(time);
    }

    /**
     * Appends the start and end date-times in ISO format to the shared save-line
     * format, e.g. {@code E | 0 | 2019-12-02T14:00 | 2019-12-02T16:00 | project meeting}.
     */
    @Override
    public String toSaveFormat() {
        return toSaveLine(from.toString(), to.toString());
    }

    /** Appends user-friendly start and end date-times to the shared task format. */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from.format(DISPLAY_DATE_TIME_FORMAT) + " to: "
                + to.format(DISPLAY_DATE_TIME_FORMAT) + ")";
    }
}
