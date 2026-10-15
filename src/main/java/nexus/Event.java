package nexus;

/** A task with a start date/time and an end date/time. */
public class Event extends Task {
    private final String from;
    private final String to;

    /** Creates an event task with its description, start time, and end time. */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EVENT;
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
