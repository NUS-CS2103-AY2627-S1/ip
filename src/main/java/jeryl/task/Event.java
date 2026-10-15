package jeryl.task;

import java.time.LocalDate;

/**
 * A task that starts on a specific date and ends on a specific date.
 */
public class Event extends Task {
    protected LocalDate from;
    protected LocalDate to;

    /**
     * Creates a not-yet-done event task spanning the given start and
     * end dates, with no priority set.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Creates a not-yet-done event task spanning the given start and
     * end dates, with the given priority.
     */
    public Event(String description, LocalDate from, LocalDate to, Priority priority) {
        super(description, priority);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return getPriorityTag() + "[E]" + super.toString() + " (from: " + from.format(DateFormats.DISPLAY)
                + " to: " + to.format(DateFormats.DISPLAY) + ")";
    }

    @Override
    public String toFileString() {
        return withPriority("E | " + super.toFileString() + " | " + from + " | " + to);
    }
}
