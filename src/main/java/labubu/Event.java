package labubu;

import java.time.LocalDateTime;

/**
 * Represents a task that happens within a specified period.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an event task with the given title and time period.
     *
     * @param taskTitle Title of the task.
     * @param from Start time of the event.
     * @param to End time of the event.
     */
    public Event(String taskTitle, LocalDateTime from, LocalDateTime to) {
        super(taskTitle);
        this.from = from;
        this.to = to;
    }

    @Override
    public String getTaskDescription() {
        return taskTitle + " (from: " + from.format(OUTPUT_FORMATTER)
                + ", to: " + to.format(OUTPUT_FORMATTER) + ")";
    }

    @Override
    public String getMarker() {
        return "E";
    }

    @Override
    public String toSaveFormat() {
        return getMarker() + "|" + getStatus().name() + "|" + taskTitle + "|" + from + "|" + to;
    }
}
