package pdd.task;

import java.time.LocalDate;
import java.util.Optional;

/** A task that needs to be done by a specific date. */
public class Deadline extends Task {
    protected LocalDate by;

    /** Creates a not-done deadline with the given description and due date. */
    public Deadline(String description, LocalDate by) {
        super(description);
        assert by != null : "by must not be null; toString() and occursOn() rely on it";
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }

    /** Serializes this task to a single line in the save-file format. */
    public String toFileFormat() {
        return "D" + FIELD_SEPARATOR + getStatusValue() + FIELD_SEPARATOR + description + FIELD_SEPARATOR + by;
    }

    /** {@inheritDoc} A deadline occurs on the date it's due. */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.equals(date);
    }

    /** {@inheritDoc} A deadline sorts by its due date. */
    @Override
    public Optional<LocalDate> getSortDate() {
        return Optional.of(by);
    }
}
