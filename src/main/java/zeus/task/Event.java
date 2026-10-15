package zeus.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task that takes place between specified start and end dates. */
public class Event extends Task {
    /** Format used to present stored dates to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates an incomplete event task.
     * @param description description of the event
     * @param startDate date on which the event starts
     * @param endDate date on which the event ends
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns this event in the format stored on disk.
     * @return serialized event task
     */
    @Override
    public String toDataString() {
        return "E | " + super.toDataString() + " | " + startDate + " | " + endDate;
    }

    /**
     * Returns this task with its event type icon and start/end text.
     * @return formatted event task
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + startDate.format(DISPLAY_DATE_FORMAT)
                + " to: " + endDate.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
