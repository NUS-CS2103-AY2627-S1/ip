package will.task;

import java.time.LocalDate;

/** A task that must be done by a given date/time, e.g. "return book by Sunday". */
public class Deadline extends Task {
    protected FlexibleDate by;

    /**
     * Creates a deadline task due at the given time.
     *
     * @param description What needs to be done.
     * @param by When it's due. Recognized as a date if it matches
     *           yyyy-MM-dd (see {@link FlexibleDate}), otherwise kept as
     *           free text.
     */
    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        this.by = new FlexibleDate(by);
    }

    /** @return This deadline's display line, with its due date appended. */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by.toDisplayString() + ")";
    }

    /** @return This deadline's save-file line, with its due date appended. */
    @Override
    public String toSaveFormat() {
        return super.toSaveFormat() + " | " + by.toSaveString();
    }

    /** @return Whether this deadline's due date equals {@code date}. */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate byDate = by.getDate();
        return byDate != null && byDate.equals(date);
    }
}
