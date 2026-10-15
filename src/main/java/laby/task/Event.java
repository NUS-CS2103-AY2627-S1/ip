package laby.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import laby.Parser;

/** Represents a task occurring between a start time and an end time. */
public class Event extends Task {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    /**
     * Creates an incomplete event task.
     *
     * @param description Description of the task.
     * @param startTime Start of the event.
     * @param endTime End of the event.
     */
    public Event(String description, LocalDateTime startTime, LocalDateTime endTime) {
        this(description, startTime, endTime, false);
    }

    /**
     * Creates an event task with the supplied completion state.
     *
     * @param description Description of the task.
     * @param startTime Start of the event.
     * @param endTime End of the event.
     * @param isDone Whether the task is complete.
     */
    public Event(String description, LocalDateTime startTime, LocalDateTime endTime, boolean isDone) {
        super(description, isDone);

        assert startTime != null : "starting time cannot be null";
        assert endTime != null : "ending time cannot be null";

        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns the event in the format shown in the console.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy HH:mm", Locale.ENGLISH);
        return "[E]" + super.toString() + " (from: " + this.startTime.format(formatter)
                + " to: " + this.endTime.format(formatter) + ")";
    }

    /**
     * Returns the event in the format used for persistence.
     */
    @Override
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "E" + Parser.FIELD_SEPARATOR + super.toFileString() + Parser.FIELD_SEPARATOR
                + this.startTime.format(formatter) + Parser.FIELD_SEPARATOR
                + this.endTime.format(formatter) + "\n";
    }
}
