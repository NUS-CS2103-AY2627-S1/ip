package computah.task;

import java.time.LocalDateTime;

import computah.util.DateTimeUtil;

/**
 * Represents a task that starts and ends at specific dates or times.
 */
public class Event extends Task {
    /**
     * Start date/time of this event.
     */
    private LocalDateTime from;

    /**
     * End date/time of this event.
     */
    private LocalDateTime to;

    /**
     * Creates an event task with the given description, start date/time, and end date/time.
     *
     * @param description description of the event task.
     * @param from start date/time of the event.
     * @param to end date/time of the event.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event task type icon.
     *
     * @return "E".
     */
    @Override
    public String getTaskType() {
        return "E";
    }

    /**
     * Converts this event into one line for the save file.
     *
     * @return save-file representation of this event.
     */
    @Override
    public String toFileString() {
        return super.toFileString() + " | " + DateTimeUtil.formatForFile(from) + " | "
                + DateTimeUtil.formatForFile(to);
    }

    /**
     * Returns the display representation of this event.
     *
     * @return task details formatted for display.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + DateTimeUtil.formatForDisplay(from) + " to: "
                + DateTimeUtil.formatForDisplay(to) + ")";
    }
}
