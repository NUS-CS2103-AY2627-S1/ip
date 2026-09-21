package answerme.task;

import java.time.LocalDateTime;
import java.util.Objects;

import answerme.parser.DateTimeParser;

/**
 * Represents a task that must be completed by a specified date
 * and/or time.
 */
public class Deadline extends Task {
    protected LocalDateTime deadline;

    /**
     * Creates a deadline task with the specified description and deadline
     * (i.e. {@code deadline}).
     *
     * @param description The deadline's description.
     * @param deadline The date and time by which the deadline must be completed.
     */
    public Deadline(String description, LocalDateTime deadline) {
        super(description);
        assert deadline != null;

        this.deadline = deadline;
    }

    /**
     * {@inheritDoc}
     *
     * Returns the deadline's fields in a human-readable format.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimeParser.formatDateTime(deadline) + ")";
    }

    /**
     * {@inheritDoc}
     *
     * Returns the deadline in storage format.
     */
    @Override
    public String toStorageFormat() {
        return getLogFormat("Deadline") + " | " + DateTimeParser.formatDateTime(deadline);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Deadline other) {
            return description.equals(other.description)
                    && deadline.equals(other.deadline);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(description, deadline);
    }
}
