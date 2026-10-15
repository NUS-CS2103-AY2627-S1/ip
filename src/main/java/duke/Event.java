package duke;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** A task with a specified start and end date or time. */
public class Event extends Task {

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");

    /** User-provided event start text. */
    private final LocalDateTime from;

    /** User-provided event end text. */
    private final LocalDateTime to;

    /** Creates an event task. */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    /** Returns the event task type icon. */
    @Override
    public String getTypeIcon() {
        return "E";
    }

    /** Returns the description and formatted event interval. */
    @Override
    public String getDisplayText() {
        return getDescription() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }

    /** Serializes this event for persistent storage. */
    @Override
    public String toSaveString() {
        return "E | " + getSaveStatus() + " | " + getDescription()
                + " | " + from + " -> " + to;
    }
}
