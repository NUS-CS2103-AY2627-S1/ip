package sheppy.task;

import sheppy.SheppyException;

/** Represents a task that takes place over a specified time range. */
public class Event extends Task {
    /** The starting date or time of the event. */
    private final String from;

    /** The ending date or time of the event. */
    private final String to;

    /**
     * Creates an event task.
     *
     * @param description the text describing the event
     * @param from the event's starting date or time
     * @param to the event's ending date or time
     * @throws SheppyException if the description, start, or end is empty
     */
    public Event(String description, String from, String to) throws SheppyException {
        super(description);
        if (from == null || from.isBlank() || to == null || to.isBlank()) {
            throw new SheppyException("an event needs non-empty /from and /to times.");
        }
        validateStorageField(from);
        validateStorageField(to);
        this.from = from;
        this.to = to;
    }

    /** @return the event task type */
    @Override
    public TaskType getTaskType() {
        return TaskType.EVENT;
    }

    /** @return the description followed by the event time range */
    @Override
    public String getDisplayDescription() {
        return super.getDisplayDescription() + " (from: " + from + " to: " + to + ")";
    }

    /** @return the task serialized with its start and end times */
    @Override
    public String toStorageString() {
        return super.toStorageString() + " | " + from + " | " + to;
    }
}
