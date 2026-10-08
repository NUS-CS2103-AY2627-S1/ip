package rudra.task;

import java.util.List;

/**
 * Represents an event task with a start and end time.
 */
public class Event extends Task {
    private final TaskDateTime from;
    private final TaskDateTime to;

    /**
     * Creates an event task with the given time range.
     *
     * @param description Description of the event.
     * @param from Start time entered by the user.
     * @param to End time entered by the user.
     */
    public Event(String description, TaskDateTime from, TaskDateTime to) {
        super(TaskType.EVENT, description);
        assert from != null : "An event must have a start date";
        assert to != null : "An event must have an end date";

        this.from = from;
        this.to = to;
    }

    /**
     * Adds the start and end timestamps to the base task storage fields.
     *
     * @return Storage fields for an event task.
     */
    @Override
    protected List<String> getStorageFields() {
        List<String> storageFields = super.getStorageFields();
        addStorageFields(storageFields, this.from.toStorageString(), this.to.toStorageString());
        return storageFields;
    }

    /**
     * Returns the user-facing text for this event task.
     *
     * @return Formatted event task description.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + this.from.toDisplayString()
                + " to: " + this.to.toDisplayString() + ")";
    }
}
