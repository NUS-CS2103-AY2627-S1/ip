package cooper.task;

import java.time.LocalDateTime;

import cooper.ui.Ui;

/**
 * Represents a task that takes place over a specified date-time range.
 */
public class Event extends Task {
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    /** Creates an incomplete event with the specified description and date-time range. */
    public Event(String description, LocalDateTime startDate, LocalDateTime endDate) {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** Creates an event with the specified description, status, and date-time range. */
    public Event(String description, boolean isDone, LocalDateTime startDate, LocalDateTime endDate) {
        super(description, isDone);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** Returns the display form of this event, including its date-time range. */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + String.format(" (from: %s to: %s)",
                        Ui.formatDate(startDate), Ui.formatDate(endDate));
    }

    /** Returns the storage representation of this event. */
    @Override
    public String toDataString() {
        return String.format("%s | %s | %s | %s", "E",
                super.toDataString(), startDate, endDate);
    }
}
