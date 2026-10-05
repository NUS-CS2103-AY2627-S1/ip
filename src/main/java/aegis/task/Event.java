package aegis.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that takes place from a start time to an end time.
 */
public class Event extends Task {

    protected LocalDate start;
    protected LocalDate end;

    /**
     * Constructs an event task.
     *
     * @param description Description of the task.
     * @param start Start time of the event.
     * @param end End time of the event.
     * @param isDone Whether the task has been marked as done.
     */
    public Event(String description, LocalDate start, LocalDate end, boolean isDone) {
        super(description, isDone);
        assert start != null : "Event start date should not be null";
        assert end != null : "Event end date should not be null";
        this.start = start;
        this.end = end;
    }


    @Override
    public LocalDate getDate() {
        return start;
    }

    @Override
    public String getTypeIcon() {
        return "E";
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description
                + " (from: " + start.format(formatter)
                + " to: " + end.format(formatter) + ")";
    }

    @Override
    public String getFileSaveFormat() {
        return "E | " + (isDone ? "1" : "0") + " | " + description + " | " + start + " | " + end;
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && start.equals(((Event) other).start)
                && end.equals(((Event) other).end);
    }


}
