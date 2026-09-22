package steph.task;

import java.time.LocalDateTime;

import steph.DateTimes;

/**
 * A task that must be done by a certain date, optionally with a time of day.
 */
public class Deadline extends Task {

    private final LocalDateTime by;

    /**
     * Constructs a deadline with the given name and due date-time.
     *
     * @param name Description of the task.
     * @param by   Date-time the task is due by.
     */
    public Deadline(String name, LocalDateTime by) {
        super(name);
        this.by = by;
    }

    /**
     * Returns the full save-file line for this deadline, e.g.
     * {@code "D | 0 | return book | 2019-10-15T18:00"}.
     */
    @Override
    public String toFileFormat() {
        return "D" + super.toFileFormat() + " " + FIELD_SEPARATOR + " " + DateTimes.toStorageFormat(this.by);
    }

    /**
     * Returns this deadline as
     * {@code "[D][ ] return book (by: Oct 15 2019)"}.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimes.toDisplayFormat(this.by) + ")";
    }
}
