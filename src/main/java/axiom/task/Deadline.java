package axiom.task;

import java.time.LocalDateTime;
import java.util.Optional;

import axiom.parser.DateTimeParser;

/**
 * Represents a deadline task that must be done by a specific date or time.
 */
public class Deadline extends Task {
    private static final String TYPE_PREFIX = "[D]";

    private final LocalDateTime by;

    /**
     * Creates a new deadline task.
     *
     * @param description Description of the deadline.
     * @param by Date and time by which the task must be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        // DateTimeParser always returns a parsed value; a null /by means a caller bug.
        assert by != null : "Deadline /by date-time should not be null";
        this.by = by;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasSameDetails(Task other) {
        if (!super.hasSameDetails(other)) {
            return false;
        }
        Deadline deadline = (Deadline) other;
        return by.equals(deadline.by);
    }

    /**
     * Returns the deadline date and time.
     *
     * @return The {@code /by} date and time.
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<LocalDateTime> getChronologicalDate() {
        return Optional.of(by);
    }

    /**
     * Returns a string representation with the {@code [D]} type prefix and formatted deadline.
     *
     * @return Formatted deadline string.
     */
    @Override
    public String toString() {
        return TYPE_PREFIX + super.toString() + " (by: " + DateTimeParser.format(by) + ")";
    }
}
