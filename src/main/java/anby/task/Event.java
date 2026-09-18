package anby.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that takes place over a date range.
 */
public class Event extends Task {
    private LocalDate from;
    private LocalDate to;

    /**
     * Creates an event task with the given name, start date, and end date.
     *
     * @param name event description
     * @param from start date in yyyy-MM-dd format
     * @param to end date in yyyy-MM-dd format
     * @throws IllegalArgumentException if the start date is after the end date
     */
    public Event(String name, String from, String to) {
        super(name);
        this.from = LocalDate.parse(from);
        this.to = LocalDate.parse(to);

        if (this.from.isAfter(this.to)) {
            throw new IllegalArgumentException("From date: " + this.from + " is after To date: " + this.to);
        }
    }

    /**
     * Converts this event into the format used in the data file.
     *
     * @return file storage representation of this event
     */
    @Override
    public String toFileString() {
        return "E | " + super.toFileString() + " | " + this.from + " | " + this.to;
    }

    /**
     * Returns the user-facing representation of this event.
     *
     * @return display string for this event
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + this.from.format(DateTimeFormatter.ofPattern("MMM dd yyyy")) + ", to: "
                + this.to.format(DateTimeFormatter.ofPattern("MMM dd yyyy")) + ")";
    }
}
