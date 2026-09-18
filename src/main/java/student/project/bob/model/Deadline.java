package student.project.bob.model;

import java.time.temporal.Temporal;

import student.project.bob.util.DateTimeParser;

/**
 * A task that must be completed before a specified date or time.
 */
public class Deadline extends Task {

    private final Temporal by;

    /**
     * Creates a deadline and parses its date or time detail.
     *
     * @param description description of the deadline
     * @param by ISO date or local date-time detail
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = DateTimeParser.parse(by);
    }

    /**
     * Returns the stored deadline date or local date-time.
     *
     * @return stored date or local date-time
     */
    public Temporal getBy() {
        return by;
    }

    /**
     * Returns this deadline in Bob's display and persistence format.
     *
     * @return the deadline's status, description, and due date or time
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimeParser.format(by) + ")";
    }
}
