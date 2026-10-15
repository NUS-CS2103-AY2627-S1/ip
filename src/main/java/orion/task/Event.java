package orion.task;

import java.time.LocalDateTime;

/**
 * Represents a task that occurs within a specific time frame.
 */
public class Event extends Task {
    protected LocalDateTime start;
    protected LocalDateTime end;

    /**
     * Initializes a new Event task with the specified description, start time, and end time.
     *
     * @param description Description of the event.
     * @param start Date and time when the event starts.
     * @param end Date and time when the event ends.
     */
    public Event(String description, LocalDateTime start, LocalDateTime end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    /**
     * Initializes a new Event task with the specified description, start time, end time, and completion status.
     *
     * @param description Description of the event.
     * @param start Date and time when the event starts.
     * @param end Date and time when the event ends.
     * @param isDone {@code true} if the task is completed, {@code false} otherwise.
     */
    public Event(String description, LocalDateTime start, LocalDateTime end, boolean isDone) {
        super(description, isDone);
        this.start = start;
        this.end = end;
    }

    @Override
    public String getTypeIcon() {
        return "E";
    }

    @Override
    public String toFileFormat() {
        return super.toFileFormat() + " | " + start.format(SAVE_FORMAT) + " | " + end.format(SAVE_FORMAT);
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + start.format(OUTPUT_FORMAT) + "; to: " + end.format(OUTPUT_FORMAT) + ")";
    }
}
