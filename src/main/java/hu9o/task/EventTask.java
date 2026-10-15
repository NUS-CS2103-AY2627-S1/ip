package hu9o.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Represents a task that takes place during a specified time.
 */
public class EventTask extends Task {
    private final LocalDateTime fromSpecification;
    private final LocalDateTime toSpecification;

    /**
     * Creates an unfinished event task.
     *
     * @param description       the event's description.
     * @param fromSpecification the start time, such as {@code "12/08/26 2 PM"}.
     * @param toSpecification   the end time, such as {@code "12/08/26 4 PM"}.
     * @throws DateTimeParseException if either time is not a valid date-time.
     */
    public EventTask(String description, String fromSpecification, String toSpecification)
            throws DateTimeParseException {
        super(description, "E");
        this.fromSpecification = DateTime.parseString(fromSpecification);
        this.toSpecification = DateTime.parseString(toSpecification);
    }

    /**
     * Returns whether this event's end time is earlier than its start time.
     *
     * @return {@code true} if the event would finish before it begins.
     */
    public boolean endsBeforeStart() {
        return toSpecification.isBefore(fromSpecification);
    }

    @Override
    public String compressionString() {
        return super.compressionString() + DateTime.formatForStorage(fromSpecification) + "|"
                + DateTime.formatForStorage(toSpecification) + "|";
    }

    @Override
    protected String specificationSuffix() {
        return " (from: " + DateTime.formatForDisplay(fromSpecification) + " to: "
                + DateTime.formatForDisplay(toSpecification)
                + ")";
    }
}
