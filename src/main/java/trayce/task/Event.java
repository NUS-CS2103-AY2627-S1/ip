package trayce.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Represents a task that occurs between a start and end time. */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd uuuu");
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an event task.
     *
     * @param description the event description
     * @param from the start date
     * @param to the end date
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    /**
     * {@inheritDoc}
     *
     * @return the type icon "E" representing an event task
     */
    @Override
    public String getTypeIcon() {
        return "E";
    }

    /**
     * {@inheritDoc}
     *
     * @return the formatted event date range representation
     */
    @Override
    public String getDateTimeDetails() {
        return " (from: " + from.format(DISPLAY_FORMAT) + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns the event start date.
     *
     * @return the start date
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns the event end date.
     *
     * @return the end date
     */
    public LocalDate getTo() {
        return to;
    }
}
