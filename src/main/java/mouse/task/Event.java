package mouse.task;

import mouse.MouseException;

/**
 * A task that starts and ends at given dates or times.
 */
public class Event extends Task {
    private final ParsedWhen from;
    private final ParsedWhen to;

    /**
     * Creates an event with a start and end time.
     *
     * @param description Task description.
     * @param from Start date or time text.
     * @param to End date or time text.
     * @throws MouseException If a dated value is invalid, or start is not before end.
     */
    public Event(String description, String from, String to) throws MouseException {
        super(description);
        this.from = ParsedWhen.parse(from, "The '/from' time");
        this.to = ParsedWhen.parse(to, "The '/to' time");
        if (this.from.canCompareTo(this.to) && this.from.compareTo(this.to) >= 0) {
            throw new MouseException("The '/from' time must be earlier than the '/to' time GRR");
        }
    }

    /**
     * Returns the event with type letter and from/to range.
     *
     * @return Display string for this event.
     */
    @Override
    public String toString() {
        return "[" + TaskType.EVENT.getSymbol() + "]" + super.toString()
                + " (from: " + from.toDisplayString() + " to: " + to.toDisplayString() + ")";
    }

    /**
     * Returns this event in save format.
     *
     * @return Encoded event line.
     */
    @Override
    public String encode() {
        return encodeFields(TaskType.EVENT.getSymbol(), from.toSaveString(), to.toSaveString());
    }

    /**
     * Returns whether {@code other} is the same event crumb.
     *
     * @param other Task to compare.
     * @return {@code true} if type, name, and range match.
     */
    @Override
    public boolean isDuplicateOf(Task other) {
        if (!super.isDuplicateOf(other)) {
            return false;
        }
        Event event = (Event) other;
        return from.toSaveString().equalsIgnoreCase(event.from.toSaveString())
                && to.toSaveString().equalsIgnoreCase(event.to.toSaveString());
    }
}
