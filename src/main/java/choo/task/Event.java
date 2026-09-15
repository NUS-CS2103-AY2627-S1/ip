package choo.task;

/**
 * Represents a task that takes place between two dates or times.
 */
public class Event extends Task {
    private final String startText;
    private final String endText;

    /**
     * Creates an incomplete Event with its start and end text.
     *
     * @param description description of the task.
     * @param startText Start date or time as entered by the user.
     * @param endText End date or time as entered by the user.
     */
    public Event(String description, String startText, String endText) {
        super(description);
        this.startText = startText;
        this.endText = endText;
    }

    /**
     * Returns the event start text as entered by the user.
     *
     * @return event start text.
     */
    public String getStartText() {
        return this.startText;
    }

    /**
     * Returns the event end text as entered by the user.
     *
     * @return event end text.
     */
    public String getEndText() {
        return this.endText;
    }

    /**
     * Returns this task with its Event type marker and time range.
     *
     * @return formatted Event.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + this.startText + " to: " + this.endText + ")";
    }
}
