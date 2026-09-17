package meow;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents an event task with a start and end date.
 */
public class Event extends Task {
    static final String STORAGE_TYPE = "E";

    protected LocalDate fromDate;
    protected LocalDate toDate;

    /**
     * Creates an event task with the specified description and date period.
     *
     * @param description the description of the event
     * @param fromDate the start date of the event
     * @param toDate the end date of the event
     */
    public Event(String description, LocalDate fromDate, LocalDate toDate) {
        super(description);
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    /**
     * Returns the start date of the event.
     *
     * @return the event start date
     */
    public LocalDate getFromDate() {
        return fromDate;
    }

    /**
     * Returns the end date of the event.
     *
     * @return the event end date
     */
    public LocalDate getToDate() {
        return toDate;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
        return "[E][" + super.getStatusForDisplay() + "] "
                + super.getDescription()
                + " (from: " + fromDate.format(formatter)
                + " to: " + toDate.format(formatter) + ")";
    }

    @Override
    public String toFileString() {
        return STORAGE_TYPE + " | " + super.getStatusForFile() + " | "
                + super.getDescription()
                + " | " + fromDate + " | " + toDate;
    }
}
