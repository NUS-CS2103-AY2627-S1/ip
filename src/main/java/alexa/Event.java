package alexa;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** A task with a start date and an end date. */
public class Event extends Task {
    /** The input format accepted by Alexa commands and used in saved data. */
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    /** The friendlier date format displayed to the user. */
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an event task.
     *
     * @param description The task description.
     * @param from The event start date.
     * @param to The event end date.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns whether another task is an identical event.
     *
     * @param other The task to compare with this event.
     * @return {@code true} if both events have the same description, start date, and end date.
     */
    @Override
    public boolean hasSameIdentity(Task other) {
        if (!(other instanceof Event event)) {
            return false;
        }
        return super.hasSameIdentity(event)
                && from.equals(event.from)
                && to.equals(event.to);
    }

    /**
     * Returns this event in the line format used for persistence.
     *
     * @return the saved representation of this event
     */
    @Override
    public String toStorageString() {
        return "E | " + (isDone() ? "1" : "0") + " | " + getDescription()
                + " | " + from.format(INPUT_FORMAT) + " | " + to.format(INPUT_FORMAT);
    }

    /**
     * Returns this event in Alexa's user-facing display format.
     *
     * @return the formatted event
     */
    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "] " + getDescription()
                + " (from: " + from.format(DISPLAY_FORMAT) + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }
}