package rene.task;

/**
 * Represents a task that takes place between a start and end date or time.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description the text describing the task.
     * @param from the event's starting date or time.
     * @param to the event's ending date or time.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EVENT;
    }

    /**
     * Returns the event's starting date or time.
     *
     * @return the event start value.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event's ending date or time.
     *
     * @return the event end value.
     */
    public String getTo() {
        return to;
    }

    @Override
    Task copy() {
        Event copy = new Event(getDescription(), from, to);
        if (isDone()) {
            copy.markAsDone();
        }
        return copy;
    }

    /**
     * Returns the event in the chatbot's display format.
     *
     * @return the basic task details followed by the start and end values.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
