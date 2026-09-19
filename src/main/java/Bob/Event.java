package bob;

import java.time.LocalDate;

/** A task representing an event with a start and end date. */
public class Event extends Task {
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an event with a start and end date.
     *
     * @param item Event description.
     * @param from Event start date.
     * @param to Event end date.
     */
    public Event(String item, LocalDate from, LocalDate to) {
        super(item);
        if (from == null || to == null) {
            throw new IllegalArgumentException("An event must have start and end dates");
        }
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("An event must end after it starts");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the storage representation of this event.
     *
     * @return Storage line.
     */
    @Override
    public String toStorageString() {
        return EVENT_TYPE + " | " + (isDone() ? COMPLETE_FLAG : "0") + " | " + getItem()
                + " | " + from + " | " + to;
    }

    /**
     * Returns the display representation of this event.
     *
     * @return Display form of this event.
     */
    @Override
    public String toString() {
        return "[E][" + (isDone() ? "X" : " ") + "] " + getItem()
                + " (from: " + from.format(DateFormats.DISPLAY_DATE)
                + " to: " + to.format(DateFormats.DISPLAY_DATE) + ")";
    }
}
