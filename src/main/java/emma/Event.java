package emma;

import java.time.LocalDate;

/**
 * A task that runs between a start and an end date.
 */
public class Event extends Task {

    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an event.
     *
     * @param description task description.
     * @param from the date the event starts.
     * @param to the date the event ends.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    protected String getTypeIcon() {
        return "E";
    }

    /**
     * Checks whether this event is running on a date, counting the first and last days.
     *
     * @param date the date to check.
     * @return true if the date falls between the start and the end.
     */
    public boolean isOn(LocalDate date) {
        return !from.isAfter(date) && !to.isBefore(date);
    }

    /**
     * Two events match only if they also run between the same two dates.
     *
     * <p>The check that both are events has to come first: it is what makes the cast
     * below safe, and this is asked about every task in the list, most of which are
     * not events.
     */
    @Override
    public boolean isDuplicateOf(Task other) {
        if (!super.isDuplicateOf(other)) {
            return false;
        }
        Event otherEvent = (Event) other;
        return from.equals(otherEvent.from) && to.equals(otherEvent.to);
    }

    /** Adds the start and end dates to the fields every task saves. */
    @Override
    public String toJson() {
        return buildJsonObject(Json.buildField("from", from.toString()),
                Json.buildField("to", to.toString()));
    }

    /** Appends the start and end dates to the standard rendering. */
    @Override
    public String toString() {
        return super.toString() + " (from: " + Dates.format(from) + " to: " + Dates.format(to) + ")";
    }
}
