package maggigorengayam.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import maggigorengayam.util.DateTimeUtil;

/** A task with a single "by" date/time, e.g. from `deadline ... /by ...`. */
public class Deadline extends Task {
    protected LocalDate byDate;
    protected LocalTime byTime;

    /** Creates a deadline with the given description and "by" date; {@code byTime} may be {@code null}. */
    public Deadline(String description, LocalDate byDate, LocalTime byTime) {
        super(description);
        // Unlike byTime, byDate is required: both call sites (Parser, Storage) only ever
        // reach this constructor with a date that DateTimeUtil#parse already succeeded on.
        assert byDate != null : "byDate must not be null; only byTime is optional";
        this.byDate = byDate;
        this.byTime = byTime;
    }

    /** True only when {@code date} is exactly the deadline's "by" date. */
    @Override
    public boolean occursOn(LocalDate date) {
        return byDate.equals(date);
    }

    /** Same as {@link Task#hasSameDetailsAs}, additionally requiring an identical "by" date and time. */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        if (!super.hasSameDetailsAs(other)) {
            return false;
        }
        Deadline that = (Deadline) other;
        return byDate.equals(that.byDate) && Objects.equals(byTime, that.byTime);
    }

    /** e.g. "[D][ ] return book (by: Dec 2 2019)" or "... (by: Dec 2 2019, 6pm)". */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimeUtil.formatForDisplay(byDate, byTime) + ")";
    }

    /** e.g. "D | 0 | return book | 2019-12-02" or "... | 2019-12-02 1800". */
    @Override
    public String toSaveFormat() {
        return "D | " + getStatusFlag() + " | " + description + " | " + DateTimeUtil.formatForSave(byDate, byTime);
    }
}
