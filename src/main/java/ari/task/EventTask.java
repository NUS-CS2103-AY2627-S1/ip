package ari.task;

import ari.Parser;

/**
 * Represents a task that takes place between a start and end time.
 */
public class EventTask extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an event task from separately parsed fields.
     *
     * @param description Event description.
     * @param from Start time of the event.
     * @param to End time of the event.
     */
    public EventTask(String description, String from, String to) {
        super(description, "E");
        this.from = Parser.parseDateTime(from);
        this.to = Parser.parseDateTime(to);
    }

    @Override
    public EventTask copy() {
        EventTask copy = new EventTask(this.description, this.from, this.to);
        copy.isDone = this.isDone;
        return copy;
    }

    /**
     * Returns the start and end times associated with this event.
     *
     * @return Start and end time text, in that order.
     */
    public String[] getEventTime() {
        return new String[] {this.from, this.to};
    }

    /**
     * Returns this event task as a storage record.
     *
     * @return Storage representation of this event task.
     */
    @Override
    public String toDataString() {
        String status = this.isDone
                ? "1"
                : "0";

        return String.format(
                "%s | %s | %s | %s | %s",
                this.type,
                status,
                this.description,
                this.from,
                this.to
        );
    }

    /**
     * Returns this event task in a user-readable format.
     *
     * @return User-readable representation of this event task.
     */
    @Override
    public String toString() {
        String box;

        if (this.isDone) {
            box = "[X]";
        } else {
            box = "[ ]";
        }

        return String.format(
                "[%s]%s %s (from: %s to: %s)",
                this.type, box, this.description, this.from, this.to);
    }
}
