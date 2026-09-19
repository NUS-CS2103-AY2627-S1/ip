package eli;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Represents a task that has a start time and an end time.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an event task.
     *
     * @param description the event description
     * @param from the start time text entered by the user
     * @param to the end time text entered by the user
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.from = TaskDateTimeFormatter.formatDateTime(from);
        this.to = TaskDateTimeFormatter.formatDateTime(to);
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }

    @Override
    Optional<LocalDateTime> getSortDate() {
        return TaskDateTimeFormatter.parseDisplayDateTime(from);
    }

    @Override
    boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && from.equalsIgnoreCase(((Event) other).from)
                && to.equalsIgnoreCase(((Event) other).to);
    }
}
