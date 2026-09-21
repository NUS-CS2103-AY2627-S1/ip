package margit.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Represents a task that occurs from one date or time to another. */
public class EventTask extends Task {
    private static final String EVENT_TYPE = "E";

    private final TaskDateTime from;
    private final TaskDateTime to;

    /** Creates an event task with its description, start, and end. */
    public EventTask(String description, TaskDateTime from, TaskDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns whether this event occurs on {@code date}, including its start and end dates. */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate fromDate = from.toLocalDate();
        LocalDate toDate = to.toLocalDate();
        if (fromDate == null || toDate == null) {
            return false;
        }

        boolean isOnOrAfterStartDate = !date.isBefore(fromDate);
        boolean isOnOrBeforeEndDate = !date.isAfter(toDate);
        return isOnOrAfterStartDate && isOnOrBeforeEndDate;
    }

    /** Returns the event start time used to sort this task. */
    @Override
    public LocalDateTime getScheduledDateTime() {
        return from.toSortDateTime();
    }

    /** Returns the save-file representation of this event task. */
    @Override
    public String toSaveFormat() {
        return EVENT_TYPE + SAVE_FIELD_SEPARATOR + getSaveStatusAndDescription()
                + SAVE_FIELD_SEPARATOR + from.toSaveFormat() + SAVE_FIELD_SEPARATOR + to.toSaveFormat();
    }

    /** Returns the user-facing representation with the event time range. */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
