package topaz.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task with a start and end time.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu HHmm", Locale.ENGLISH);
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final boolean hasFromTime;
    private final boolean hasToTime;

    /**
     * Creates an incomplete event task.
     *
     * @param description the task description
     * @param from the event start date and time
     * @param to the event end date and time
     * @param hasFromTime whether the input included a start time
     * @param hasToTime whether the input included an end time
     */
    public Event(String description, LocalDateTime from, LocalDateTime to,
                 boolean hasFromTime, boolean hasToTime) {
        super(description);
        // Parsed events always have both endpoints; display and persistence use both values.
        assert from != null : "An event must have a start date and time.";
        assert to != null : "An event must have an end date and time.";
        this.from = from;
        this.to = to;
        this.hasFromTime = hasFromTime;
        this.hasToTime = hasToTime;
    }

    /**
     * Returns the event task type.
     *
     * @return the event task type
     */
    @Override
    public TaskType getTaskType() {
        return TaskType.EVENT;
    }

    /**
     * Returns the description together with the formatted event times.
     *
     * @return the formatted event description
     */
    @Override
    public String getDescription() {
        String formattedFrom = hasFromTime ? from.format(DISPLAY_FORMAT) : from.toLocalDate().format(
                DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH));
        String formattedTo = hasToTime ? to.format(DISPLAY_FORMAT) : to.toLocalDate().format(
                DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH));
        return super.getDescription() + " (from: " + formattedFrom + " to: " + formattedTo + ")";
    }

    @Override
    protected boolean hasSameSchedule(Task other) {
        return from.equals(((Event) other).from) && to.equals(((Event) other).to);
    }

    /**
     * Serializes this event for storage.
     *
     * @return the event in save-file format
     */
    @Override
    public String toFileString() {
        String savedFrom = hasFromTime ? from.toString() : from.toLocalDate().toString();
        String savedTo = hasToTime ? to.toString() : to.toLocalDate().toString();
        return "E | " + (isDone() ? "1" : "0") + " | " + super.getDescription()
                + " | " + savedFrom + " | " + savedTo;
    }
}
