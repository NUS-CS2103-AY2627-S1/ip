package linus.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a type of Task with a start and end date.
 */
public class Event extends Task {
    /** Date that the event starts on */
    private LocalDate start;
    /** Date that the event ends on */
    private LocalDate end;

    /**
     * Creates a new Event task.
     *
     * @param isDone Whether the task has been marked as completed.
     * @param description Description of the task.
     * @param start Date that the event starts on.
     * @param end Date that the event ends on.
     */
    public Event(boolean isDone, String description, LocalDate start, LocalDate end) {
        super(isDone, description);
        this.start = start;
        this.end = end;
    }

    /**
     * Returns a String representation of each Event task.
     * All event tasks are represented starting with [E].
     * Start and end are formatted as d MMM yyyy.
     *
     * @return String representation of each Event task.
     */
    @Override
    public String toString() {
        String formattedStart = this.start.format(DateTimeFormatter.ofPattern("d MMM yyyy"));
        String formattedEnd = this.end.format(DateTimeFormatter.ofPattern("d MMM yyyy"));
        return "[E]" + super.toString() + " (from: " + formattedStart + " to: " + formattedEnd + ")";
    }

    /**
     * Returns a String representation of each Event task that
     * is formatted for ease of storing into the storage file.
     *
     * @return Formatted String representation of Event task.
     */
    @Override
    public String toFileFormat() {
        if (this.isDone()) {
            return "E | X | " + this.getDescription() + " | " + this.start + " | " + this.end + "\n";
        }
        return "E | | " + this.getDescription() + " | " + this.start + " | " + this.end + "\n";
    }

    /**
     * Returns if the input object is equal to the event task.
     * Event tasks are equal if they have the same description, start date and end date.
     * @param obj   the reference object with which to compare.
     * @return Whether the 2 objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Event) {
            Event task = (Event) obj;
            return this.getDescription().equals(task.getDescription())
                    && this.start.equals(task.start)
                    && this.end.equals(task.end);
        }
        return false;
    }
}
