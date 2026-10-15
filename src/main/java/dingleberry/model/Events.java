package dingleberry.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that spans a start and end date/time.
 */
public final class Events extends Task {
    /** Stores the event start date and time. */
    private final LocalDateTime fromDateTime;
    /** Stores the event end date and time. */
    private final LocalDateTime toDateTime;

    /**
     * Creates an incomplete event occurring between the given date and times.
     *
     * @param description the user-visible event description.
     * @param from the event start date and time.
     * @param to the event end date and time.
     */
    public Events(final String description, final LocalDateTime from,
                  final LocalDateTime to) {
        super(description);
        this.fromDateTime = from;
        this.toDateTime = to;
    }

    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(),
                fromDateTime.format(DISPLAY_DATE_TIME_FORMAT),
                toDateTime.format(DISPLAY_DATE_TIME_FORMAT));
    }

    @Override
    public String toSaveFormat() {
        return "E | " + encodeCommonFields() + " | "
            + fromDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            + " | "
            + toDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
