package nexus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** A task with stated start and end times. */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd uuuu");
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an event task.
     *
     * @param description the event description.
     * @param from start text.
     * @param to end text.
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.from = LocalDate.parse(from);
        this.to = LocalDate.parse(to);
        if (!this.to.isAfter(this.from)) {
            throw new IllegalArgumentException(
                    "An event's /to date must be after its /from date.");
        }
    }

    /** Returns the event start text. */
    public String getFrom() {
        return from.toString();
    }

    /** Returns the event end text. */
    public String getTo() {
        return to.toString();
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }
}
