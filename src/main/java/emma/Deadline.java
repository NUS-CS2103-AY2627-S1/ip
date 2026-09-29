package emma;

import java.time.LocalDate;

/**
 * A task that must be finished by a given date.
 */
public class Deadline extends Task {

    private final LocalDate by;

    /**
     * Creates a deadline.
     *
     * @param description what the user typed.
     * @param by the date the task is due.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    @Override
    protected String getTypeIcon() {
        return "D";
    }

    /**
     * Checks whether this deadline falls on or before a date.
     *
     * @param date the latest date to allow.
     * @return true if this deadline is due no later than the date.
     */
    public boolean isDueBy(LocalDate date) {
        return !by.isAfter(date);
    }

    /** Two deadlines match only if they are also due on the same date. */
    @Override
    public boolean isDuplicateOf(Task other) {
        return super.isDuplicateOf(other) && by.equals(((Deadline) other).by);
    }

    /** Adds the due date to the fields every task saves. */
    @Override
    public String toJson() {
        return buildJsonObject(Json.buildField("by", by.toString()));
    }

    /** Appends the due date to the standard rendering. */
    @Override
    public String toString() {
        return super.toString() + " (by: " + Dates.format(by) + ")";
    }
}
