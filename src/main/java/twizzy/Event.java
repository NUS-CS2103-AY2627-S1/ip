package twizzy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that takes place between specified start and end dates.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("d MMM uuuu", Locale.ENGLISH);

    protected LocalDate from;

    protected LocalDate to;

    /**
     * Creates an incomplete event task.
     *
     * @param description description of the task
     * @param from start date
     * @param to end date
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start date.
     *
     * @return start date
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns the event end date.
     *
     * @return end date
     */
    public LocalDate getTo() {
        return to;
    }

    /**
     * Formats this task with its start and end details.
     *
     * @return display form of this event
     */
    @Override
    public String toString() {
        return super.toString() + System.lineSeparator() + "  ↳ Schedule: " + from.format(DISPLAY_FORMAT)
                + " → " + to.format(DISPLAY_FORMAT);
    }
}
