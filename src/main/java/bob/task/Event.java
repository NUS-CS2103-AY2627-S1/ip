package bob.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that takes place between a start and an end date and time.
 */
public class Event extends Task {
    private static final DateTimeFormatter STORAGE_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm");
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter
            .ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an incomplete event task.
     *
     * @param description description of the event
     * @param from date and time at which the event starts
     * @param to date and time at which the event ends
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        this(description, from, to, Priority.NONE);
    }

    /**
     * Creates an incomplete task with an explicit priority.
     *
     * @param description description of the task
     * @param from start time
     * @param to end time
     * @param priority priority tag, or NONE for no tag
     */
    public Event(String description, LocalDateTime from, LocalDateTime to, Priority priority) {
        super(description, priority);
        this.from = from;
        this.to = to;
    }

    /**
     * Serializes this event with its task type, start, and end time.
     *
     * @return one event record suitable for storage
     */
    @Override
    public String toStorageString() {
        return "E | " + super.toStorageString() + " | "
                + from.format(STORAGE_FORMAT) + " | " + to.format(STORAGE_FORMAT) + getStoragePriority();
    }

    /**
     * Formats this event for display with friendly start and end times.
     *
     * @return display form of this event
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }
}
