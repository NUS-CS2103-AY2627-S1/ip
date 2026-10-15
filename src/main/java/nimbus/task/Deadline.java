package nimbus.task;

import java.time.LocalDate;

/**
 * Represents a Deadline task in the Nimbus application.
 */
public class Deadline extends Task {

    private final LocalDate by;

    /**
     * Constructs a Deadline object, with an additional by date.
     *
     * @param name The name of the deadline.
     * @param by   The date of the deadline.
     */
    public Deadline(String name, LocalDate by) {
        super(name, TaskType.DEADLINE);
        this.by = by;
    }

    public String getBy() {
        return by.format(OUTPUT_FORMAT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toFileString() {
        return super.toFileString() + "|" + by.toString();
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + getBy() + ")";
    }
}
