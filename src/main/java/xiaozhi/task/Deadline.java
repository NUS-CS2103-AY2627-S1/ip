package xiaozhi.task;

import java.time.LocalDate;
import java.util.Objects;

import xiaozhi.util.Dates;

/**
 * A task that must be done by a particular date.
 */
public class Deadline extends Task {
    protected LocalDate byDate;

    /**
     * Creates a deadline due by the given date.
     *
     * @param description What the deadline is.
     * @param byDate Date the deadline is due by.
     */
    public Deadline(String description, LocalDate byDate) {
        super(description);
        assert byDate != null
                : "Deadline's by-date should never be null; it is always the result of Dates.parse(), "
                + "which either returns a valid date or throws.";
        this.byDate = byDate;
    }

    @Override
    public String getTypeIcon() {
        return "D";
    }

    @Override
    public String toSaveFormat() {
        return super.toSaveFormat() + " | " + byDate;
    }

    /**
     * Returns whether {@code other} is a Deadline with the same description
     * and {@code /by} date as this one. Completion status is not part of the
     * comparison; used by {@link xiaozhi.command.AddCommand} to reject adding
     * the same deadline twice.
     *
     * @param other The object to compare against.
     * @return {@code true} if {@code other} is an equivalent Deadline, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Deadline)) {
            return false;
        }
        Deadline that = (Deadline) other;
        return description.equals(that.description) && byDate.equals(that.byDate);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getTypeIcon(), description, byDate);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + Dates.format(byDate) + ")";
    }
}
