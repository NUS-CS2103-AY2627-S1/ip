package yapbot.task;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Represents an event task with a start and end time.
 */
public class Event extends Task {

    protected String from;
    protected String to;

    /**
     * Creates an event task with the given description, start time and end time.
     *
     * @param description the task description.
     * @param from the start time of the event.
     * @param to the end time of the event.
     */
    public Event(String description, String from, String to) {
        super(description);
        assert from != null && !from.isBlank() : "Parser/Storage must validate 'from' beforehand";
        assert to != null && !to.isBlank() : "Parser/Storage must validate 'to' beforehand";
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the type icon used to identify an event task in the save file.
     *
     * @return "E".
     */
    @Override
    protected String getTypeIcon() {
        return "E";
    }

    /**
     * Returns whether this event's end time has already passed, i.e. is
     * strictly before today. Since {@link #to} is free text (e.g. "4pm")
     * rather than a required date, this is a best-effort check: it only
     * returns {@code true} when {@code to} happens to be a valid
     * {@code yyyy-mm-dd} date that is before today; any other text (or a
     * date of today) returns {@code false}.
     *
     * @return {@code true} if {@code to} is a valid date before today.
     */
    public boolean isOverdue() {
        try {
            return LocalDate.parse(to).isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Returns this event task serialized for the save file, including the start and end times.
     *
     * @return the file-format representation of this task.
     */
    @Override
    public String toFileFormat() {
        return appendPriorityField(super.toFileFormat() + " | " + from + " | " + to);
    }

    /**
     * Returns this event as it should be displayed to the user, e.g.
     * {@code "[E][ ]project meeting (from: Mon 2pm to: 4pm)"}.
     *
     * @return the display representation of this task.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    /**
     * {@inheritDoc} An event additionally must share the same from/to times,
     * matched case-insensitively since they are free-text (e.g. "Mon 2pm").
     */
    @Override
    public boolean isDuplicateOf(Task other) {
        return super.isDuplicateOf(other)
                && from.equalsIgnoreCase(((Event) other).from)
                && to.equalsIgnoreCase(((Event) other).to);
    }
}